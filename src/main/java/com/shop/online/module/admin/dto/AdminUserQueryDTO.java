package com.shop.online.module.admin.dto;

import com.shop.online.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理端用户查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理端用户查询请求")
public class AdminUserQueryDTO extends PageQuery {

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "关键词（用户名/手机号）")
    private String keyword;
}
