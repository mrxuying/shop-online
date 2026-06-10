package com.shop.online.common.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PayStatusEnum {

    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    REFUNDED(3, "已退款");

    private final Integer code;
    private final String desc;

    PayStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
