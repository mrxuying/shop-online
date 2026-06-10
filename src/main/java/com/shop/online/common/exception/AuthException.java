package com.shop.online.common.exception;

import com.shop.online.common.result.ResultCode;
import lombok.Getter;

/**
 * 认证异常
 */
@Getter
public class AuthException extends RuntimeException {

    private final Integer code;

    public AuthException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public AuthException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}
