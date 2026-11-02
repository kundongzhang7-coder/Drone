package com.example.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 接口请求日志拦截器：记录请求路径、方法和处理耗时。
 * 后续如果需要登录鉴权（如校验 Token），可以在 preHandle 里统一处理。
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLogInterceptor.class);

    private static final String START_TIME_ATTR = "REQUEST_START_TIME";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        log.info("==> {} {}", request.getMethod(), request.getRequestURI());
        // 鉴权示例：如果以后加 Token，在这里校验，返回 false 即拦截
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        Object startTime = request.getAttribute(START_TIME_ATTR);
        long cost = startTime instanceof Long ? System.currentTimeMillis() - (Long) startTime : -1;
        log.info("<== {} {} status={} cost={}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), cost);
        if (ex != null) {
            log.error("请求处理异常: {}", ex.getMessage());
        }
    }
}
