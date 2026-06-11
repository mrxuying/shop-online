package com.shop.online.module.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址实体
 */
@Data
public class UserAddress {

    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
