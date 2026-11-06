package com.drone.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一业务状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录状态已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    ERROR(500, "服务器内部错误"),

    LOGIN_FAILED(1001, "用户名或密码错误"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    USER_DISABLED(1003, "账号已被禁用"),
    TOKEN_INVALID(1004, "令牌无效"),
    REPEAT_SUBMIT(1005, "请勿重复提交"),

    TASK_NOT_FOUND(2001, "检测任务不存在"),
    TASK_STATUS_ERROR(2002, "任务状态不允许该操作"),

    MQ_CONSUME_FAIL(3001, "消息消费失败"),
    AI_SERVICE_UNAVAILABLE(4001, "AI 服务暂不可用");

    private final Integer code;
    private final String msg;
}
