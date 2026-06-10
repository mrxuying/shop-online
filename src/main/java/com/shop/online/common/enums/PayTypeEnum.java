package com.shop.online.common.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PayTypeEnum {

    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信");

    private final Integer code;
    private final String desc;

    PayTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
