package com.shop.online.module.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表项返回
 */
@Data
@Schema(description = "订单列表项")
public class OrderVO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单编号", example = "2024010100001")
    private String orderNo;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "待付款")
    private String statusDesc;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付方式")
    private Integer payType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
