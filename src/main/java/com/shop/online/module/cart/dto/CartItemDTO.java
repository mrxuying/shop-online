package com.shop.online.module.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车商品请求
 */
@Data
@Schema(description = "购物车商品请求")
public class CartItemDTO {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @NotNull(message = "SKU ID不能为空")
    @Schema(description = "SKU ID", example = "1")
    private Long skuId;

    @Min(value = 1, message = "数量最少为1")
    @Schema(description = "数量", example = "2")
    private Integer quantity = 1;

    @Schema(description = "是否选中", example = "true")
    private Boolean selected = true;
}
