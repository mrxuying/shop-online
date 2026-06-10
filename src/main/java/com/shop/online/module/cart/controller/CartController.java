package com.shop.online.module.cart.controller;

import com.shop.online.common.result.Result;
import com.shop.online.infrastructure.security.UserContext;
import com.shop.online.module.cart.dto.CartItemDTO;
import com.shop.online.module.cart.dto.CartSelectAllDTO;
import com.shop.online.module.cart.service.ICartService;
import com.shop.online.module.cart.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "购物车管理")
public class CartController {

    private final ICartService cartService;

    @GetMapping
    @Operation(summary = "获取购物车")
    public Result<CartVO> getCart() {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(cartService.getCart(userId));
    }

    @PostMapping
    @Operation(summary = "添加商品到购物车")
    public Result<Void> addItem(@Valid @RequestBody CartItemDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        cartService.addItem(userId, dto);
        return Result.success();
    }

    @PutMapping("/{skuId}")
    @Operation(summary = "修改购物车商品数量")
    public Result<Void> updateItem(@PathVariable Long skuId, @RequestParam Integer quantity) {
        Long userId = UserContext.getCurrentUserId();
        cartService.updateItem(userId, skuId, quantity);
        return Result.success();
    }

    @DeleteMapping("/{skuId}")
    @Operation(summary = "删除购物车商品")
    public Result<Void> removeItem(@PathVariable Long skuId) {
        Long userId = UserContext.getCurrentUserId();
        cartService.removeItem(userId, skuId);
        return Result.success();
    }

    @PutMapping("/select-all")
    @Operation(summary = "全选/取消全选")
    public Result<Void> selectAll(@Valid @RequestBody CartSelectAllDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        cartService.selectAll(userId, dto);
        return Result.success();
    }
}
