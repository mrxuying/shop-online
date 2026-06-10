package com.shop.online.module.product.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品查询请求（分页+筛选+排序）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品查询请求")
public class ProductQueryDTO extends PageQuery {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "排序字段: price-价格 sales-销量 time-上架时间", example = "price")
    private String sortField;

    @Schema(description = "排序方式: asc-升序 desc-降序", example = "asc")
    private String sortOrder;

    @Schema(description = "状态: 1-上架 0-下架")
    private Integer status;
}
