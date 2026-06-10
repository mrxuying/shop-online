package com.shop.online.module.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 管理员登录返回
 */
@Data
@Builder
@Schema(description = "管理员登录返回")
public class AdminLoginVO {

    @Schema(description = "管理员ID", example = "1")
    private Long adminId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickname;

    @Schema(description = "角色", example = "SUPER_ADMIN")
    private String role;

    @Schema(description = "Access Token")
    private String accessToken;
}
