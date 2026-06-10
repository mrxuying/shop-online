package com.shop.online.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token 刷新请求
 */
@Data
@Schema(description = "Token刷新请求")
public class RefreshTokenDTO {

    @NotBlank(message = "Refresh Token不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
