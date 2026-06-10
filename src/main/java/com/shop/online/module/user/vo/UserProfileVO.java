package com.shop.online.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个人信息返回
 */
@Data
@Schema(description = "用户个人信息")
public class UserProfileVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "手机号", example = "138****8000")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别: 0-未知 1-男 2-女", example = "1")
    private Integer gender;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;
}
