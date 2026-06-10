package com.shop.online.module.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价返回
 */
@Data
@Schema(description = "商品评价")
public class ProductReviewVO {

    @Schema(description = "评价ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "评分: 1-5", example = "5")
    private Integer rating;

    @Schema(description = "评价内容", example = "质量很好，非常满意")
    private String content;

    @Schema(description = "评价图片")
    private String images;

    @Schema(description = "评价时间")
    private LocalDateTime createTime;
}
