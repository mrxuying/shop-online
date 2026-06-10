package com.shop.online.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
@TableName("product_review")
public class ProductReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("order_no")
    private String orderNo;

    @TableField("product_id")
    private Long productId;

    @TableField("sku_id")
    private Long skuId;

    private Integer rating;
    private String content;
    private String images;

    @TableField("is_hidden")
    private Integer isHidden;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
