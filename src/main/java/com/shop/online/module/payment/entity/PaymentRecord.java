package com.shop.online.module.payment.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
public class PaymentRecord {

    private Long id;
    private String orderNo;
    private String payNo;
    private Integer payType;
    private BigDecimal payAmount;
    private Integer status;
    private String thirdPartyNo;
    private LocalDateTime payTime;
    private LocalDateTime refundTime;
    private LocalDateTime createTime;
}
