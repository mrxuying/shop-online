package com.shop.online.module.order.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
public class Order {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal freightAmount;
    private BigDecimal payAmount;
    private Integer payType;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String deliveryCompany;
    private String deliveryNo;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime paymentTime;
    private LocalDateTime cancelTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
