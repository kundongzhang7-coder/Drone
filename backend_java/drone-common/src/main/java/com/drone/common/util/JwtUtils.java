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
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
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
