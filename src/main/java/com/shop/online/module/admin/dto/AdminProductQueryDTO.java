package com.shop.online.module.admin.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理端商品查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理端商品查询请求")
public class AdminProductQueryDTO extends PageQuery {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "状态: 0-下架 1-上架")
    private Integer status;

    @Schema(description = "关键词（商品名称）")
    private String keyword;
}
