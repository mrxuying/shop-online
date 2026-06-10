package com.shop.online.module.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付请求
 */
@Data
@Schema(description = "支付请求")
public class PayRequestDTO {

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: 1-支付宝 2-微信", example = "1")
    private Integer payType;
}
