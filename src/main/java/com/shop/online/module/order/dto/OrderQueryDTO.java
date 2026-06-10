package com.shop.online.module.order.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "订单查询请求")
public class OrderQueryDTO extends PageQuery {

    @Schema(description = "订单状态: 1-待付款 2-待发货 3-待收货 4-已完成 5-已取消", example = "1")
    private Integer status;
}
