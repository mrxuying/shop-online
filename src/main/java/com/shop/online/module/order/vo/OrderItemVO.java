package com.shop.online.module.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品项返回
 */
@Data
@Schema(description = "订单商品项")
public class OrderItemVO {

    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "SKU ID", example = "1")
    private Long skuId;

    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "规格信息")
    private String specInfo;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "小计金额")
    private BigDecimal totalAmount;
}
