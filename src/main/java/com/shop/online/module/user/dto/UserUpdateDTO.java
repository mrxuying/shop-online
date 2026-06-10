package com.shop.online.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息修改请求
 */
@Data
@Schema(description = "用户信息修改请求")
public class UserUpdateDTO {

    @Size(max = 32, message = "昵称长度不能超过32位")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别: 0-未知 1-男 2-女", example = "1")
    private Integer gender;
}
