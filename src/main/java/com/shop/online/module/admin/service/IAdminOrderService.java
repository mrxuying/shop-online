package com.shop.online.module.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.online.module.admin.dto.AdminOrderQueryDTO;
import com.shop.online.module.admin.dto.DeliverDTO;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderVO;

/**
 * 管理端订单服务接口
 */
public interface IAdminOrderService {

    /**
     * 订单列表（多条件查询）
     */
    IPage<OrderVO> pageOrders(AdminOrderQueryDTO dto);

    /**
     * 订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId);

    /**
     * 发货
     */
    void deliver(Long orderId, DeliverDTO dto);

    /**
     * 退款处理
     */
    void refund(Long orderId);
}
