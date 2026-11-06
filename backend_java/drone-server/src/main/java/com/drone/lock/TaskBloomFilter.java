package com.drone.lock;

import com.drone.common.constant.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 检测任务 ID 布隆过滤器：在缓存和数据库之前拦截一定不存在的请求，
 * 与空值缓存共同构成缓存穿透防护。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskBloomFilter {

    private final RedissonClient redissonClient;
    private RBloomFilter<Long> bloomFilter;

    @jakarta.annotation.PostConstruct
    public void init() {
        bloomFilter = redissonClient.getBloomFilter(RedisConstants.BLOOM_TASK_KEY);
        // 预期 100 万元素、误判率 1%；已初始化时 tryInit 返回 false，不报错
        bloomFilter.tryInit(1_000_000L, 0.01);
    }

    public void add(Long id) {
        bloomFilter.add(id);
    }

    /**
     * 返回 false 表示一定不存在；返回 true 表示可能存在（允许极小误判率）
     */
    public boolean mightContain(Long id) {
        return bloomFilter.contains(id);
    }
}
