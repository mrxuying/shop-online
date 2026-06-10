package com.shop.online.module.admin.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface IAdminStatisticsService {

    /**
     * 数据概览
     */
    Map<String, Object> getOverview();

    /**
     * 销售趋势（近7天/30天）
     */
    List<Map<String, Object>> getSalesTrend(Integer days);
}
