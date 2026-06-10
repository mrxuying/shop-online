package com.shop.online.module.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品保存请求（新增/编辑）
 */
@Data
@Schema(description = "商品保存请求")
public class ProductSaveDTO {

    @Schema(description = "商品ID（编辑时必填）")
    private Long id;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "7")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    private String name;

    @Schema(description = "副标题", example = "A17 Pro 芯片")
    private String subtitle;

    @Schema(description = "主图URL")
    private String mainImage;

    @Schema(description = "商品详情(HTML)")
    private String detail;

    @NotNull(message = "价格不能为空")
    @Schema(description = "最低价格", example = "8999.00")
    private BigDecimal price;

    @Schema(description = "状态: 0-下架 1-上架", example = "1")
    private Integer status;
}
