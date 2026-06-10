package com.shop.online.module.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
@TableName("payment_record")
public class PaymentRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("pay_no")
    private String payNo;

    @TableField("pay_type")
    private Integer payType;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    private Integer status;

    @TableField("third_party_no")
    private String thirdPartyNo;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("refund_time")
    private LocalDateTime refundTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
