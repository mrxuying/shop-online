package com.shop.online.module.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "购物车")
public class CartVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车商品列表")
    private List<CartItemVO> items;

    @Schema(description = "选中商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "选中商品总数量")
    private Integer totalQuantity;

    @Schema(description = "是否全选")
    private Boolean allSelected;
}
