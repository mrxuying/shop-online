package com.shop.online.module.product.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
public class Category {

    private Long id;
    private Long parentId;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
