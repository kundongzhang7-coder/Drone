package com.drone.lock;

import com.drone.common.constant.RedisConstants;
import com.drone.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存防护工具，覆盖三类典型高并发问题：
 * <ul>
 *     <li>穿透：空值缓存（配合布隆过滤器）</li>
 *     <li>击穿：互斥锁 / 逻辑过期</li>
 *     <li>雪崩：TTL 随机抖动</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    /** 逻辑过期缓存重建的单线程执行器，天然串行化重建任务 */
    private static final ExecutorService REBUILD_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String EMPTY_MARKER = "";

    /**
     * 缓存空值方案（防穿透），TTL 随机抖动（防雪崩）
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type,
                                          Function<ID, R> dbFallback, Duration ttl) {
        String key = keyPrefix + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return EMPTY_MARKER.equals(cached) ? null : type.cast(cached);
        }

        R dbData = dbFallback.apply(id);
        if (dbData == null) {
            redisTemplate.opsForValue().set(key, EMPTY_MARKER, RedisConstants.CACHE_NULL_TTL);
            return null;
        }
        set(key, dbData, randomTtl(ttl));
        return dbData;
    }

    /**
     * 互斥锁方案（防击穿）：只允许一个线程回源重建，其余线程重试
     */
    public <R, ID> R queryWithMutex(String keyPrefix, ID id, Class<R> type,
                                    Function<ID, R> dbFallback, Duration ttl,
                                    String lockPrefix) {
        String key = keyPrefix + id;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return EMPTY_MARKER.equals(cached) ? null : type.cast(cached);
        }

        RLock lock = redissonClient.getLock(lockPrefix + id);
        try {
            boolean locked = lock.tryLock(5, RedisConstants.LOCK_TTL.getSeconds(), TimeUnit.SECONDS);
            if (!locked) {
                Thread.sleep(50);
                return queryWithMutex(keyPrefix, id, type, dbFallback, ttl, lockPrefix);
            }

            // 拿到锁后双重检查，避免上一个线程已重建完成却重复回源
            Object doubleCheck = redisTemplate.opsForValue().get(key);
            if (doubleCheck != null) {
                return EMPTY_MARKER.equals(doubleCheck) ? null : type.cast(doubleCheck);
            }

            R dbData = dbFallback.apply(id);
            if (dbData == null) {
                redisTemplate.opsForValue().set(key, EMPTY_MARKER, RedisConstants.CACHE_NULL_TTL);
            } else {
                set(key, dbData, randomTtl(ttl));
            }
            return dbData;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("缓存重建被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 逻辑过期方案（防击穿，保证可用性优先）：过期后返回旧数据，异步重建。
     * 注意：锁的获取与释放都在重建线程内完成，避免跨线程解锁问题。
     */
    public <R, ID> R queryWithLogicExpire(String keyPrefix, ID id, Class<R> type,
                                         Function<ID, R> dbFallback, Duration ttl,
                                         String lockPrefix) {
        String key = keyPrefix + id;
        Object cached = redisTemplate.opsForValue().get(key);

        // 冷启动无缓存：同步回源一次
        if (cached == null) {
            R dbData = dbFallback.apply(id);
            if (dbData != null) {
                setWithLogicExpire(key, dbData, ttl);
            }
            return dbData;
        }

        RedisData redisData = (RedisData) cached;
        @SuppressWarnings("unchecked")
        R data = (R) type.cast(redisData.getData());

        // 未逻辑过期，直接返回
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            return data;
        }

        // 已过期：提交异步重建，当前请求立即返回旧数据
        REBUILD_EXECUTOR.submit(() -> {
            RLock lock = redissonClient.getLock(lockPrefix + id);
            boolean locked = lock.tryLock();
            if (!locked) {
                return;
            }
            try {
                // 双重检查，可能已被其他任务重建
                Object latest = redisTemplate.opsForValue().get(key);
                if (latest instanceof RedisData rd
                        && rd.getExpireTime().isAfter(LocalDateTime.now())) {
                    return;
                }
                R newData = dbFallback.apply(id);
                if (newData != null) {
                    setWithLogicExpire(key, newData, ttl);
                }
            } catch (Exception e) {
                log.error("逻辑过期缓存重建失败 key={}", key, e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });

        return data;
    }

    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, randomTtl(ttl));
    }

    public void setWithLogicExpire(String key, Object value, Duration ttl) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(ttl.getSeconds()));
        // 物理上不设 TTL，依靠逻辑过期字段
        redisTemplate.opsForValue().set(key, redisData);
    }

    /**
     * 在基础 TTL 上叠加 0-5 分钟随机抖动，避免大量 key 同时失效（雪崩）
     */
    private Duration randomTtl(Duration base) {
        long jitter = ThreadLocalRandom.current().nextLong(0, 300);
        return Duration.ofSeconds(base.getSeconds() + jitter);
    }
}
