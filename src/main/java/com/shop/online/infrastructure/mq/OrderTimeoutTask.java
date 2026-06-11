package com.shop.online.infrastructure.mq;

import com.shop.online.common.constant.AppConstants;
import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.entity.OrderItem;
import com.shop.online.module.order.mapper.OrderItemMapper;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时取消定时任务
 * 每 5 分钟扫描一次，兜底取消超时未支付订单
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductSkuMapper skuMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 每 5 分钟执行一次，扫描超时未支付订单
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void cancelTimeoutOrders() {
        log.debug("开始扫描超时未支付订单...");

        // 查询超过30分钟未支付的订单
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(AppConstants.ORDER_TIMEOUT_MINUTES);
        Order query = new Order();
        query.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        List<Order> timeoutOrders = orderMapper.selectListAdvanced(
                query, null, null, timeout);

        if (timeoutOrders.isEmpty()) {
            log.debug("无超时订单");
            return;
        }

        for (Order order : timeoutOrders) {
            log.info("自动取消超时订单, orderNo={}", order.getOrderNo());

            // 释放库存
            OrderItem itemQuery = new OrderItem();
            itemQuery.setOrderId(order.getId());
            List<OrderItem> items = orderItemMapper.selectList(itemQuery);
            for (OrderItem item : items) {
                skuMapper.releaseStock(item.getSkuId(), item.getQuantity());
            }

            // 更新订单状态
            order.setStatus(OrderStatusEnum.CANCELLED.getCode());
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);

            // 清除 Redis 超时标记
            String timeoutKey = AppConstants.ORDER_TIMEOUT_KEY_PREFIX + order.getOrderNo();
            redisTemplate.delete(timeoutKey);
        }

        log.info("超时订单取消完成, 共取消 {} 个订单", timeoutOrders.size());
    }
}
