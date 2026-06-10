package com.shop.online.module.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.online.module.order.dto.OrderCreateDTO;
import com.shop.online.module.order.dto.OrderQueryDTO;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface IOrderService {

    /**
     * 创建订单
     */
    String createOrder(Long userId, OrderCreateDTO dto);

    /**
     * 订单列表（按状态筛选）
     */
    IPage<OrderVO> pageOrders(Long userId, OrderQueryDTO dto);

    /**
     * 订单详情
     */
    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单（未付款）
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    void confirmReceive(Long userId, Long orderId);
}
