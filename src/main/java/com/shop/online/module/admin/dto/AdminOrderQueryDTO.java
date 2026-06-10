package com.shop.online.module.admin.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理端订单查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理端订单查询请求")
public class AdminOrderQueryDTO extends PageQuery {

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;
}
