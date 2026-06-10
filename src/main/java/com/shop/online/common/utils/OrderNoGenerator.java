package com.shop.online.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

/**
 * 订单编号生成器 — 雪花算法
 */
@Component
public class OrderNoGenerator {

    private final Snowflake snowflake;

    public OrderNoGenerator() {
        this.snowflake = IdUtil.getSnowflake(1, 1);
    }

    /**
     * 生成订单编号
     */
    public String generateOrderNo() {
        return snowflake.nextIdStr();
    }

    /**
     * 生成支付流水号
     */
    public String generatePayNo() {
        return "PAY" + snowflake.nextIdStr();
    }
}
