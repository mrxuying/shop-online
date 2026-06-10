package com.shop.online.common.exception;

import com.shop.online.common.result.ResultCode;
import lombok.Getter;

/**
 * 权限异常 — 用户无权限访问
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final Integer code;

    public ForbiddenException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public ForbiddenException(String message) {
        super(message);
        this.code = ResultCode.FORBIDDEN.getCode();
    }
}
