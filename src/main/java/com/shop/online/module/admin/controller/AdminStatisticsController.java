package com.shop.online.module.admin.controller;

import com.shop.online.common.result.Result;
import com.shop.online.module.admin.service.IAdminStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端 — 数据统计控制器
 */
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "管理端 — 数据统计")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    private final IAdminStatisticsService adminStatisticsService;

    @GetMapping("/overview")
    @Operation(summary = "数据概览")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(adminStatisticsService.getOverview());
    }

    @GetMapping("/sales-trend")
    @Operation(summary = "销售趋势")
    public Result<List<Map<String, Object>>> getSalesTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(adminStatisticsService.getSalesTrend(days));
    }
}
