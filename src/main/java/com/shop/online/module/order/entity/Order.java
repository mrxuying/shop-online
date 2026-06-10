package com.shop.online.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("freight_amount")
    private BigDecimal freightAmount;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("pay_type")
    private Integer payType;

    private Integer status;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_address")
    private String receiverAddress;

    @TableField("delivery_company")
    private String deliveryCompany;

    @TableField("delivery_no")
    private String deliveryNo;

    @TableField("delivery_time")
    private LocalDateTime deliveryTime;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("payment_time")
    private LocalDateTime paymentTime;

    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
