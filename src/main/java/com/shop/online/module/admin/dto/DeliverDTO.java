package com.shop.online.module.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发货请求
 */
@Data
@Schema(description = "发货请求")
public class DeliverDTO {

    @NotBlank(message = "快递公司不能为空")
    @Schema(description = "快递公司", example = "顺丰速运")
    private String deliveryCompany;

    @NotBlank(message = "快递单号不能为空")
    @Schema(description = "快递单号", example = "SF1234567890")
    private String deliveryNo;
}
