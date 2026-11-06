package com.drone.common.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密工具：BCrypt 加盐哈希，不引入整套 Spring Security 过滤链。
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
