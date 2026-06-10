package com.shop.online.module.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车商品项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "购物车商品项")
public class CartItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private BigDecimal subTotal;

    @Schema(description = "是否选中")
    private Boolean selected;

    @Schema(description = "库存数量")
    private Integer stock;
}
