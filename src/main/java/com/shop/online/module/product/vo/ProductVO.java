package com.shop.online.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品列表项返回
 */
@Data
@Schema(description = "商品列表项")
public class ProductVO {

    @Schema(description = "商品ID", example = "1")
    private Long id;

    @Schema(description = "分类ID", example = "7")
    private Long categoryId;

    @Schema(description = "商品名称", example = "iPhone 15")
    private String name;

    @Schema(description = "副标题/卖点", example = "A17 Pro 芯片")
    private String subtitle;

    @Schema(description = "主图URL")
    private String mainImage;

    @Schema(description = "价格", example = "5999.00")
    private BigDecimal price;

    @Schema(description = "累计销量", example = "1000")
    private Integer sales;

    @Schema(description = "状态: 0-下架 1-上架", example = "1")
    private Integer status;
}
