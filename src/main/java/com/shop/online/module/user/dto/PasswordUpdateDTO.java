package com.shop.online.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 密码修改请求
 */
@Data
@Schema(description = "密码修改请求")
public class PasswordUpdateDTO {

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "123456")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度6-32位")
    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
