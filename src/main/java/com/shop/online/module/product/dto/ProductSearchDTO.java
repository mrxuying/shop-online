package com.shop.online.module.product.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品搜索请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品搜索请求")
public class ProductSearchDTO extends PageQuery {

    @NotBlank(message = "搜索关键词不能为空")
    @Schema(description = "搜索关键词", example = "手机")
    private String keyword;
}
