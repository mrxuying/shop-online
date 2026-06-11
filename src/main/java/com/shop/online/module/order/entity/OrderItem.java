package com.shop.online.module.order.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品项实体
 */
@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private String orderNo;
    private Long productId;
    private Long skuId;
    private String productName;
    private String productImage;
    private String specInfo;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
}
