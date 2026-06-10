package com.shop.online.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品SKU返回
 */
@Data
@Schema(description = "商品SKU")
public class ProductSkuVO {

    @Schema(description = "SKU ID", example = "1")
    private Long id;

    @Schema(description = "SKU编码", example = "IP15-256-RED")
    private String skuCode;

    @Schema(description = "规格信息", example = "{\"颜色\":\"红色\",\"容量\":\"256G\"}")
    private String specInfo;

    @Schema(description = "SKU价格", example = "6999.00")
    private BigDecimal price;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "SKU图片")
    private String image;
}
