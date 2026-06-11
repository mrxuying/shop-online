package com.shop.online.module.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体
 */
@Data
public class AdminUser {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
