package com.shop.online.module.product.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片实体
 */
@Data
public class ProductImage {

    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
