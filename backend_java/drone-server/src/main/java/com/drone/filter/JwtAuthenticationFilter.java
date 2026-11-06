package com.drone.filter;

import com.drone.common.constant.JwtClaimsConstants;
import com.drone.common.constant.RedisConstants;
import com.drone.common.context.LoginUser;
import com.drone.common.context.UserContext;
import com.drone.common.properties.JwtProperties;
import com.drone.common.result.Result;
import com.drone.common.result.ResultCode;
import com.drone.common.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器：每个请求执行一次。
 *
 * <p>职责：解析令牌签名与有效期 → 校验 Redis 登录白名单 → 写入用户上下文 → 滑动续期。
 * 白名单路径直接放行；受保护路径任何一环失败都返回 401。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /** 无需登录即可访问的路径前缀 */
    private static final List<String> WHITE_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isWhitePath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null) {
            writeUnauthorized(response, ResultCode.UNAUTHORIZED.getMsg());
            return;
        }

        try {
            Claims claims = jwtUtils.parse(token);

            String whitelistKey = RedisConstants.LOGIN_TOKEN_KEY + token;
            Boolean exists = stringRedisTemplate.hasKey(whitelistKey);
            if (Boolean.FALSE.equals(exists)) {
                writeUnauthorized(response, "登录状态已失效，请重新登录");
                return;
            }

            Long userId = Long.valueOf(claims.get(JwtClaimsConstants.USER_ID).toString());
            String username = (String) claims.get(JwtClaimsConstants.USERNAME);
            UserContext.set(new LoginUser(userId, username));

            // 滑动过期：每次有效访问都刷新白名单 TTL
            stringRedisTemplate.expire(whitelistKey, RedisConstants.LOGIN_TOKEN_TTL);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.warn("令牌校验失败: {}", e.getMessage());
            writeUnauthorized(response, ResultCode.TOKEN_INVALID.getMsg());
        } finally {
            UserContext.clear();
        }
    }

    private boolean isWhitePath(String path) {
        for (String white : WHITE_PATHS) {
            if (path.equals(white) || path.startsWith(white + "/") || path.startsWith(white)) {
                return true;
            }
        }
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        if (header == null || header.isBlank()) {
            return null;
        }
        String prefix = jwtProperties.getPrefix();
        if (header.startsWith(prefix)) {
            return header.substring(prefix.length()).trim();
        }
        return header.trim();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.UNAUTHORIZED.getCode(), message)));
    }
}
