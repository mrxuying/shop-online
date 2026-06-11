package com.shop.online.module.order.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shop.online.common.constant.AppConstants;
import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.ResultCode;
import com.shop.online.common.utils.OrderNoGenerator;
import com.shop.online.module.cart.service.ICartService;
import com.shop.online.module.cart.vo.CartItemVO;
import com.shop.online.module.cart.vo.CartVO;
import com.shop.online.module.order.dto.OrderCreateDTO;
import com.shop.online.module.order.dto.OrderQueryDTO;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.entity.OrderItem;
import com.shop.online.module.order.mapper.OrderItemMapper;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.order.service.IOrderService;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderItemVO;
import com.shop.online.module.order.vo.OrderVO;
import com.shop.online.module.product.entity.ProductSku;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import com.shop.online.module.user.entity.UserAddress;
import com.shop.online.module.user.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductSkuMapper skuMapper;
    private final UserAddressMapper addressMapper;
    private final ICartService cartService;
    private final OrderNoGenerator orderNoGenerator;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public String createOrder(Long userId, OrderCreateDTO dto) {
        // 获取购物车中已选择的商品
        CartVO cart = cartService.getCart(userId);
        List<CartItemVO> selectedItems = cart.getItems().stream()
                .filter(i -> Boolean.TRUE.equals(i.getSelected()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new BusinessException(ResultCode.ORDER_ITEM_EMPTY);
        }

        // 获取收货地址
        UserAddress address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_FOUND);
        }

        // 使用 Redisson 分布式锁锁定库存并校验
        List<String> lockKeys = selectedItems.stream()
                .map(i -> "lock:sku:" + i.getSkuId())
                .toList();

        for (int i = 0; i < selectedItems.size(); i++) {
            CartItemVO item = selectedItems.get(i);
            RLock lock = redissonClient.getLock(lockKeys.get(i));
            try {
                boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
                if (!locked) {
                    throw new BusinessException(ResultCode.SYSTEM_ERROR.getCode(), "系统繁忙，请稍后再试");
                }

                // 校验库存
                ProductSku sku = skuMapper.selectById(item.getSkuId());
                if (sku == null || sku.getStock() < item.getQuantity()) {
                    throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        // 扣减库存
        for (CartItemVO item : selectedItems) {
            int rows = skuMapper.updateStock(item.getSkuId(), item.getQuantity());
            if (rows == 0) {
                throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
            }
        }

        // 生成订单
        String orderNo = orderNoGenerator.generateOrderNo();
        BigDecimal totalAmount = cart.getTotalAmount();
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(
                address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(dto.getRemark());
        order.setCreateTime(now);
        order.setUpdateTime(now);

        orderMapper.insert(order);

        // 保存订单商品项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemVO item : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(item.getProductId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductImage(item.getProductImage());
            orderItem.setSpecInfo(item.getSpecInfo());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(item.getSubTotal());
            orderItem.setCreateTime(now);
            orderItems.add(orderItem);
        }
        orderItemMapper.insertBatch(orderItems);

        // 清除购物车已选商品
        cartService.clearSelected(userId);

        // 设置订单超时标记（30分钟后自动取消）
        String timeoutKey = AppConstants.ORDER_TIMEOUT_KEY_PREFIX + orderNo;
        redisTemplate.opsForValue().set(timeoutKey, "1",
                Duration.ofMinutes(AppConstants.ORDER_TIMEOUT_MINUTES));

        log.info("订单创建成功, orderNo={}, userId={}, amount={}", orderNo, userId, totalAmount);
        return orderNo;
    }

    @Override
    public PageResult<OrderVO> pageOrders(Long userId, OrderQueryDTO dto) {
        Order query = new Order();
        query.setUserId(userId);
        if (dto.getStatus() != null) {
            query.setStatus(dto.getStatus());
        }

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageHelper.orderBy("create_time desc");

        List<Order> list = orderMapper.selectList(query);
        PageInfo<Order> pageInfo = new PageInfo<>(list);
        List<OrderVO> records = list.stream().map(this::toOrderVO).toList();
        return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        OrderDetailVO detailVO = toOrderDetailVO(order);

        // 查询订单商品项
        OrderItem itemQuery = new OrderItem();
        itemQuery.setOrderId(orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemQuery);
        detailVO.setItems(items.stream().map(this::toOrderItemVO).toList());

        return detailVO;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }

        // 释放库存
        releaseStock(orderId);

        // 更新订单状态
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 清除超时标记
        String timeoutKey = AppConstants.ORDER_TIMEOUT_KEY_PREFIX + order.getOrderNo();
        redisTemplate.delete(timeoutKey);

        log.info("订单取消成功, orderNo={}, userId={}", order.getOrderNo(), userId);
    }

    @Override
    @Transactional
    public void confirmReceive(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        if (!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_RECEIPT.getCode())) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CONFIRM);
        }

        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单确认收货成功, orderNo={}, userId={}", order.getOrderNo(), userId);
    }

    /**
     * 释放订单库存
     */
    private void releaseStock(Long orderId) {
        OrderItem itemQuery = new OrderItem();
        itemQuery.setOrderId(orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemQuery);
        for (OrderItem item : items) {
            skuMapper.releaseStock(item.getSkuId(), item.getQuantity());
        }
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
