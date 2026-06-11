package com.shop.online.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer gender;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
