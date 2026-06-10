package com.shop.online.module.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类保存请求
 */
@Data
@Schema(description = "分类保存请求")
public class CategorySaveDTO {

    @Schema(description = "分类ID（编辑时必填）")
    private Long id;

    @Schema(description = "父分类ID（0=一级分类）", example = "0")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "数码电子")
    private String name;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态: 0-禁用 1-启用", example = "1")
    private Integer status;
}
