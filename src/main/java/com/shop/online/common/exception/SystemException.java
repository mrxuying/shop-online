package com.shop.online.common.exception;

import com.shop.online.common.result.ResultCode;
import lombok.Getter;

/**
 * 系统异常 — 用户不可见的内部错误
 */
@Getter
public class SystemException extends RuntimeException {

    private final Integer code;

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
    }
}
