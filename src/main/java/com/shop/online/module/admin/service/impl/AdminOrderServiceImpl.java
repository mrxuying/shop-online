package com.shop.online.module.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.admin.dto.AdminOrderQueryDTO;
import com.shop.online.module.admin.dto.DeliverDTO;
import com.shop.online.module.admin.service.IAdminOrderService;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.entity.OrderItem;
import com.shop.online.module.order.mapper.OrderItemMapper;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderItemVO;
import com.shop.online.module.order.vo.OrderVO;
import com.shop.online.module.payment.entity.PaymentRecord;
import com.shop.online.module.payment.mapper.PaymentRecordMapper;
import com.shop.online.module.payment.service.IPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 管理端订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements IAdminOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final IPayService payService;

    @Override
    public IPage<OrderVO> pageOrders(AdminOrderQueryDTO dto) {
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Objects.nonNull(dto.getStatus()), Order::getStatus, dto.getStatus())
                .eq(StrUtil.isNotBlank(dto.getOrderNo()), Order::getOrderNo, dto.getOrderNo())
                .eq(Objects.nonNull(dto.getUserId()), Order::getUserId, dto.getUserId())
                .orderByDesc(Order::getCreateTime);

        return orderMapper.selectPage(page, wrapper).convert(this::toOrderVO);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        OrderDetailVO detailVO = toOrderDetailVO(order);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        detailVO.setItems(items.stream().map(this::toOrderItemVO).toList());

        return detailVO;
    }

    @Override
    @Transactional
    public void deliver(Long orderId, DeliverDTO dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_DELIVERY.getCode())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(OrderStatusEnum.PENDING_RECEIPT.getCode());
        order.setDeliveryCompany(dto.getDeliveryCompany());
        order.setDeliveryNo(dto.getDeliveryNo());
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单发货成功, orderNo={}, deliveryCompany={}", order.getOrderNo(), dto.getDeliveryCompany());
    }

    @Override
    @Transactional
    public void refund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        payService.refund(order.getOrderNo());
        log.info("订单退款处理完成, orderNo={}", order.getOrderNo());
    }

    // ==================== VO 转换 ====================

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(Objects.requireNonNull(OrderStatusEnum.of(order.getStatus())).getDesc());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayType(order.getPayType());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    private OrderDetailVO toOrderDetailVO(Order order) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(Objects.requireNonNull(OrderStatusEnum.of(order.getStatus())).getDesc());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayType(order.getPayType());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setDeliveryCompany(order.getDeliveryCompany());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setPaymentTime(order.getPaymentTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setRemark(order.getRemark());
        return vo;
    }

    private OrderItemVO toOrderItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setProductId(item.getProductId());
        vo.setSkuId(item.getSkuId());
        vo.setProductName(item.getProductName());
        vo.setProductImage(item.getProductImage());
        vo.setSpecInfo(item.getSpecInfo());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setTotalAmount(item.getTotalAmount());
        return vo;
    }
}
