package com.drone.common.constant;

import java.time.Duration;

/**
 * Redis 键前缀与 TTL 统一约定
 */
public final class RedisConstants {

    private RedisConstants() {
    }

    // ===== 登录态（Token 白名单） =====
    /** 登录令牌白名单 key 前缀，值为 userId，存在即视为有效登录 */
    public static final String LOGIN_TOKEN_KEY = "login:token:";
    /** 登录态有效期 */
    public static final Duration LOGIN_TOKEN_TTL = Duration.ofHours(2);

    // ===== 登录并发控制 =====
    /** 登录操作互斥/限流 key 前缀（按用户名） */
    public static final String LOGIN_LOCK_KEY = "login:lock:";
    /** 登录失败次数计数 key 前缀（防爆破） */
    public static final String LOGIN_FAIL_KEY = "login:fail:";
    public static final Duration LOGIN_FAIL_TTL = Duration.ofMinutes(10);
    /** 允许的最大连续登录失败次数 */
    public static final int LOGIN_FAIL_MAX = 5;

    // ===== 业务缓存 =====
    public static final String CACHE_TASK_KEY = "cache:task:";
    public static final String CACHE_STATS_KEY = "cache:stats:";
    public static final String CACHE_USER_KEY = "cache:user:";
    /** 基础缓存有效期（随机 TTL 在此基础上抖动，防雪崩） */
    public static final Duration CACHE_BASE_TTL = Duration.ofMinutes(30);
    /** 空值缓存有效期（防穿透） */
    public static final Duration CACHE_NULL_TTL = Duration.ofMinutes(2);

    // ===== 缓存重建互斥锁 =====
    public static final String LOCK_TASK_KEY = "lock:task:";
    public static final Duration LOCK_TTL = Duration.ofSeconds(10);

    // ===== 布隆过滤器 =====
    public static final String BLOOM_TASK_KEY = "bloom:task:ids";

    // ===== 幂等令牌 =====
    public static final String IDEMPOTENT_KEY = "idempotent:submit:";
    public static final Duration IDEMPOTENT_TTL = Duration.ofMinutes(5);
}
