package com.drone.common.exception;

import com.drone.common.result.ResultCode;

/**
 * 登录失败异常
 */
public class LoginFailedException extends BaseException {

    public LoginFailedException(String message) {
        super(ResultCode.LOGIN_FAILED.getCode(), message);
    }

    public LoginFailedException(ResultCode resultCode) {
        super(resultCode);
    }
}
