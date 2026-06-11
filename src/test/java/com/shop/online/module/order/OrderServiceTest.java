package com.shop.online.module.order;

import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.utils.OrderNoGenerator;
import com.shop.online.module.cart.service.ICartService;
import com.shop.online.module.order.dto.OrderQueryDTO;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.entity.OrderItem;
import com.shop.online.module.order.mapper.OrderItemMapper;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.order.service.impl.OrderServiceImpl;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderVO;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import com.shop.online.module.user.mapper.UserAddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务测试")
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductSkuMapper skuMapper;

    @Mock
    private UserAddressMapper addressMapper;

    @Mock
    private ICartService cartService;

    @Mock
    private OrderNoGenerator orderNoGenerator;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("2024010100001");
        testOrder.setUserId(1L);
        testOrder.setTotalAmount(new BigDecimal("8999.00"));
        testOrder.setPayAmount(new BigDecimal("8999.00"));
        testOrder.setDiscountAmount(BigDecimal.ZERO);
        testOrder.setFreightAmount(BigDecimal.ZERO);
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        testOrder.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("订单列表 — 按状态筛选")
    void shouldPageOrdersByStatus() {
        // Given
        OrderQueryDTO dto = new OrderQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());

        when(orderMapper.selectList(any(Order.class)))
                .thenReturn(List.of(testOrder));

        // When
        PageResult<OrderVO> result = orderService.pageOrders(1L, dto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("2024010100001", result.getRecords().get(0).getOrderNo());
    }

    @Test
    @DisplayName("订单详情 — 成功")
    void shouldGetOrderDetail() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderItemMapper.selectList(any(OrderItem.class)))
                .thenReturn(Collections.emptyList());

        // When
        OrderDetailVO result = orderService.getOrderDetail(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals("2024010100001", result.getOrderNo());
        verify(orderItemMapper).selectList(any(OrderItem.class));
    }

    @Test
    @DisplayName("订单详情 — 订单不属于当前用户")
    void shouldThrowExceptionWhenOrderNotBelongToUser() {
        // Given
        testOrder.setUserId(2L);
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        assertThrows(BusinessException.class, () -> orderService.getOrderDetail(1L, 1L));
    }

    @Test
    @DisplayName("取消订单 — 成功")
    void shouldCancelOrder() {
        // Given
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderItemMapper.selectList(any(OrderItem.class)))
                .thenReturn(Collections.emptyList());
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> orderService.cancelOrder(1L, 1L));

        // Then
        assertEquals(OrderStatusEnum.CANCELLED.getCode(), testOrder.getStatus());
        verify(orderMapper).updateById(testOrder);
    }

    @Test
    @DisplayName("取消订单 — 订单状态不允许取消")
    void shouldThrowExceptionWhenOrderCannotCancel() {
        // Given
        testOrder.setStatus(OrderStatusEnum.PENDING_DELIVERY.getCode());
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // When & Then
        assertThrows(BusinessException.class, () -> orderService.cancelOrder(1L, 1L));
    }

    @Test
    @DisplayName("确认收货 — 成功")
    void shouldConfirmReceive() {
        // Given
        testOrder.setStatus(OrderStatusEnum.PENDING_RECEIPT.getCode());
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> orderService.confirmReceive(1L, 1L));

        // Then
        assertEquals(OrderStatusEnum.COMPLETED.getCode(), testOrder.getStatus());
        assertNotNull(testOrder.getReceiveTime());
    }
}
