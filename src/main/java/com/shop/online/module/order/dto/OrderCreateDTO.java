package com.shop.online.module.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 订单创建请求
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateDTO {

    @Schema(description = "收货地址ID", example = "1")
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    @Schema(description = "备注", example = "请尽快发货")
    private String remark;
}
