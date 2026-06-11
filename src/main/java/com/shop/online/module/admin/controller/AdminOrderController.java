package com.shop.online.module.admin.controller;

import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.Result;
import com.shop.online.module.admin.dto.AdminOrderQueryDTO;
import com.shop.online.module.admin.dto.DeliverDTO;
import com.shop.online.module.admin.service.IAdminOrderService;
import com.shop.online.module.order.vo.OrderDetailVO;
import com.shop.online.module.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 — 订单管理控制器
 */
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Tag(name = "管理端 — 订单管理")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final IAdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<PageResult<OrderVO>> listOrders(@Valid AdminOrderQueryDTO dto) {
        return Result.success(adminOrderService.pageOrders(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        return Result.success(adminOrderService.getOrderDetail(id));
    }

    @PutMapping("/{id}/deliver")
    @Operation(summary = "订单发货")
    public Result<Void> deliver(@PathVariable Long id, @Valid @RequestBody DeliverDTO dto) {
        adminOrderService.deliver(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/refund")
    @Operation(summary = "退款处理")
    public Result<Void> refund(@PathVariable Long id) {
        adminOrderService.refund(id);
        return Result.success();
    }
}
