package com.shop.online.module.product.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体
 */
@Data
public class ProductSku {

    private Long id;
    private Long productId;
    private String skuCode;
    private String specInfo;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
