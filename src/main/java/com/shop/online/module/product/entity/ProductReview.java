package com.shop.online.module.product.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
public class ProductReview {

    private Long id;
    private Long userId;
    private String orderNo;
    private Long productId;
    private Long skuId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isHidden;
    private LocalDateTime createTime;
}
