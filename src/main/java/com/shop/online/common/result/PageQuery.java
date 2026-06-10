package com.shop.online.common.result;

import com.shop.online.common.constant.AppConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询请求基类
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = AppConstants.DEFAULT_PAGE_NUM;

    @Schema(description = "每页条数", example = "20")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private Integer pageSize = AppConstants.DEFAULT_PAGE_SIZE;
}
