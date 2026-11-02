package com.example.backend.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 时间工具类
 */
public final class TimeUtils {

    private TimeUtils() {
    }

    /**
     * 把前端传来的 ISO 带时区时间字符串（如 2026-11-02T06:00:00.000Z）
     * 解析为本机时区的 LocalDateTime
     */
    public static LocalDateTime parseIsoToLocal(String isoTimeStr) {
        return LocalDateTime.ofInstant(Instant.parse(isoTimeStr), ZoneId.systemDefault());
    }
}
