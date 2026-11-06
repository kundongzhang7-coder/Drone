package com.drone.common.exception;

import com.drone.common.result.ResultCode;

/**
 * 通用业务异常
 */
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(Integer code, String message) {
        super(code, message);
    }
}
