package com.shop.online.module.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付结果返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "支付结果")
public class PayResultVO {

    @Schema(description = "支付流水号", example = "PAY2024010100001")
    private String payNo;

    @Schema(description = "订单编号", example = "2024010100001")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付状态: 0-待支付 1-成功 2-失败 3-已退款", example = "1")
    private Integer status;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;
}
