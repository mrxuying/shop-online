package com.shop.online.module.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 全选/取消全选请求
 */
@Data
@Schema(description = "全选请求")
public class CartSelectAllDTO {

    @NotNull(message = "选中状态不能为空")
    @Schema(description = "是否全选", example = "true")
    private Boolean selected;
}
