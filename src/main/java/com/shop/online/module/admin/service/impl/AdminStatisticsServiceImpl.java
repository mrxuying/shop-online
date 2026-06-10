package com.shop.online.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.module.admin.service.IAdminStatisticsService;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.product.entity.Product;
import com.shop.online.module.product.mapper.ProductMapper;
import com.shop.online.module.user.entity.User;
import com.shop.online.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements IAdminStatisticsService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new LinkedHashMap<>();

        // 用户总数
        Long userCount = userMapper.selectCount(null);
        result.put("userCount", userCount);

        // 商品总数（上架）
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1));
        result.put("productCount", productCount);

        // 今日订单数
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, todayStart));
        result.put("todayOrders", todayOrders);

        // 今日销售额
        List<Order> todayPaidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, todayStart)
                        .in(Order::getStatus, OrderStatusEnum.PENDING_DELIVERY.getCode(),
                                OrderStatusEnum.PENDING_RECEIPT.getCode(),
                                OrderStatusEnum.COMPLETED.getCode()));
        BigDecimal todaySales = todayPaidOrders.stream()
                .map(Order::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("todaySales", todaySales);

        // 待处理订单（待发货）
        Long pendingDelivery = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatusEnum.PENDING_DELIVERY.getCode()));
        result.put("pendingDelivery", pendingDelivery);

        // 待处理退款
        Long pendingRefund = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, OrderStatusEnum.REFUNDING.getCode()));
        result.put("pendingRefund", pendingRefund);

        return result;
    }

    @Override
    public List<Map<String, Object>> getSalesTrend(Integer days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            List<Order> dayOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>()
                            .ge(Order::getCreateTime, dayStart)
                            .lt(Order::getCreateTime, dayEnd)
                            .in(Order::getStatus, OrderStatusEnum.PENDING_DELIVERY.getCode(),
                                    OrderStatusEnum.PENDING_RECEIPT.getCode(),
                                    OrderStatusEnum.COMPLETED.getCode()));

            BigDecimal daySales = dayOrders.stream()
                    .map(Order::getPayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("sale", daySales);
            dayData.put("count", dayOrders.size());
            trend.add(dayData);
        }

        return trend;
    }
}
