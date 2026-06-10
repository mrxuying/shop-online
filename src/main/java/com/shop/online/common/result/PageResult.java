package com.shop.online.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize;

    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, 1, 20, Collections.emptyList());
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }
}
