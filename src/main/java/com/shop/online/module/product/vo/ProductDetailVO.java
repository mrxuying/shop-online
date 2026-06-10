package com.shop.online.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情返回
 */
@Data
@Schema(description = "商品详情")
public class ProductDetailVO {

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

    @Schema(description = "商品详情(富文本)")
    private String detail;

    @Schema(description = "最低价格", example = "5999.00")
    private BigDecimal price;

    @Schema(description = "累计销量", example = "1000")
    private Integer sales;

    @Schema(description = "状态: 0-下架 1-上架", example = "1")
    private Integer status;

    @Schema(description = "SKU列表")
    private List<ProductSkuVO> skus;

    @Schema(description = "商品图片列表")
    private List<String> images;
}
