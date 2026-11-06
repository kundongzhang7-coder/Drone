package com.drone;

import com.drone.common.properties.JwtProperties;
import com.drone.common.util.JwtUtils;
import com.drone.common.util.PasswordUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 不依赖任何外部基础设施的单元测试：JWT 签发解析与 BCrypt 密码校验。
 */
class JwtAndPasswordTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        // HS256 要求至少 32 字节
        properties.setSecret("drone-detection-unit-test-secret-key-0123456789");
        properties.setExpirationHours(2);
        jwtUtils = new JwtUtils(properties);
        jwtUtils.init();
    }

    @Test
    void shouldCreateAndParseToken() {
        String token = jwtUtils.createToken(1001L, "admin");
        Claims claims = jwtUtils.parse(token);

        assertEquals(1001L, jwtUtils.getUserId(token));
        assertEquals("admin", jwtUtils.getUsername(token));
        assertEquals(1001L, Long.valueOf(claims.get("userId").toString()));
    }

    @Test
    void shouldEncodeAndVerifyPassword() {
        String raw = "admin123";
        String encoded = PasswordUtils.encode(raw);

        assertTrue(PasswordUtils.matches(raw, encoded));
        assertFalse(PasswordUtils.matches("wrong-password", encoded));
    }
}
