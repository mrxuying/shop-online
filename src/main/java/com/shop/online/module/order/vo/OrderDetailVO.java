package com.shop.online.module.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情返回
 */
@Data
@Schema(description = "订单详情")
public class OrderDetailVO {

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单编号", example = "2024010100001")
    private String orderNo;

    @Schema(description = "订单状态", example = "2")
    private Integer status;

    @Schema(description = "状态描述", example = "待发货")
    private String statusDesc;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付方式")
    private Integer payType;

    @Schema(description = "收货人")
    private String receiverName;

    @Schema(description = "收货电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "快递公司")
    private String deliveryCompany;

    @Schema(description = "快递单号")
    private String deliveryNo;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单商品列表")
    private List<OrderItemVO> items;
}
