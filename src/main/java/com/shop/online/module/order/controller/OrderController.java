package com.shop.online.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.Result;
import com.shop.online.infrastructure.security.UserContext;
import com.shop.online.module.order.dto.OrderCreateDTO;
import com.shop.online.module.order.dto.OrderQueryDTO;
import com.shop.online.module.order.service.IOrderService;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<String> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        String orderNo = orderService.createOrder(userId, dto);
        return Result.success("订单创建成功", orderNo);
    }

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<PageResult<OrderVO>> listOrders(@Valid OrderQueryDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        IPage<OrderVO> page = orderService.pageOrders(userId, dto);
        return Result.success(PageResult.of(page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(orderService.getOrderDetail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认收货")
    public Result<Void> confirmReceive(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.confirmReceive(userId, id);
        return Result.success();
    }
}
