package com.example.backend;

import com.example.backend.util.TimeUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 后端单元测试。
 *
 * <p>注意：原测试类引用了已删除的 JPA 包（entity/repository），会导致 test-compile 失败。
 * 现改为不依赖 Spring 容器与外部数据库的纯单元测试，避免在无 MySQL 环境下构建失败。</p>
 */
class BackendApplicationTests {

    /** 合法的 ISO 带时区时间字符串应能解析成功（转本机时区） */
    @Test
    void parseIsoToLocal_returnsNonNullForValidInput() {
        LocalDateTime t = TimeUtils.parseIsoToLocal("2026-11-02T06:00:00.000Z");
        assertNotNull(t);
    }

    /** 非法时间字符串应抛出解析异常 */
    @Test
    void parseIsoToLocal_throwsForInvalidInput() {
        assertThrows(DateTimeParseException.class, () -> TimeUtils.parseIsoToLocal("not-a-date"));
    }
}
