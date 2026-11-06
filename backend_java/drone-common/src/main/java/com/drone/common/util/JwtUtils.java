package com.drone.common.util;

import com.drone.common.constant.JwtClaimsConstants;
import com.drone.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具：基于 jjwt 0.12.x（Jakarta 兼容）。
 * 负责令牌的签发与解析，登录态的"是否有效"由 Redis 白名单统一裁决。
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "未配置 JWT 密钥（drone.jwt.secret / 环境变量 JWT_SECRET）。"
                    + "本地开发请使用默认的 dev profile（内置开发密钥）；"
                    + "生产环境必须通过环境变量注入至少 32 字节的随机密钥。");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT 密钥长度不足，当前 " + keyBytes.length + " 字节，HS256 要求至少 32 字节。"
                    + "请重新生成足够长的随机密钥并通过环境变量 JWT_SECRET 注入。");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 签发 JWT
     */
    public String createToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpirationHours() * 3600_000L);
        return Jwts.builder()
                .claim(JwtClaimsConstants.USER_ID, userId)
                .claim(JwtClaimsConstants.USERNAME, username)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并校验签名/过期时间，失败抛出 JwtException
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        Object value = parse(token).get(JwtClaimsConstants.USER_ID);
        return value == null ? null : Long.valueOf(value.toString());
    }

    public String getUsername(String token) {
        Object value = parse(token).get(JwtClaimsConstants.USERNAME);
        return value == null ? null : value.toString();
    }
}
