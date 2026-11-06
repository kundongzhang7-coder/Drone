package com.drone.common.context;

/**
 * 基于 ThreadLocal 的当前登录用户上下文。
 *
 * <p>分布式多实例下，每个请求线程由 JWT 过滤器写入当前用户，请求结束后必须清理，
 * 避免 Tomcat 线程池复用导致的用户信息串号。</p>
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    public static String getUsername() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
