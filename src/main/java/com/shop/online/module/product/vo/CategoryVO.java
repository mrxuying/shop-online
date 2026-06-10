package com.shop.online.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品分类返回（树形结构）
 */
@Data
@Schema(description = "商品分类")
public class CategoryVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "分类名称", example = "服装")
    private String name;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    @Schema(description = "子分类列表")
    private List<CategoryVO> children;
}
