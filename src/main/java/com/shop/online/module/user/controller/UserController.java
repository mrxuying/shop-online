package com.shop.online.module.user.controller;

import com.shop.online.common.annotation.RateLimit;
import com.shop.online.common.result.Result;
import com.shop.online.infrastructure.security.UserContext;
import com.shop.online.module.user.dto.*;
import com.shop.online.module.user.service.IUserService;
import com.shop.online.module.user.vo.UserAddressVO;
import com.shop.online.module.user.vo.UserLoginVO;
import com.shop.online.module.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final IUserService userService;

    @RateLimit(key = "rate:user:register:", count = 3, time = 60, message = "注册请求过于频繁，请1分钟后再试")
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @RateLimit(key = "rate:user:login:", count = 10, time = 60)
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public Result<UserLoginVO> refreshToken(@Valid @RequestBody RefreshTokenDTO dto) {
        return Result.success(userService.refreshToken(dto));
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserProfileVO> getProfile() {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        userService.updatePassword(userId, dto);
        return Result.success();
    }

    @GetMapping("/address")
    @Operation(summary = "收货地址列表")
    public Result<List<UserAddressVO>> listAddresses() {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.listAddresses(userId));
    }

    @PostMapping("/address")
    @Operation(summary = "新增收货地址")
    public Result<Long> createAddress(@Valid @RequestBody UserAddressDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(userService.createAddress(userId, dto));
    }

    @PutMapping("/address/{id}")
    @Operation(summary = "修改收货地址")
    public Result<Void> updateAddress(@PathVariable Long id, @Valid @RequestBody UserAddressDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateAddress(userId, id, dto);
        return Result.success();
    }

    @DeleteMapping("/address/{id}")
    @Operation(summary = "删除收货地址")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        userService.deleteAddress(userId, id);
        return Result.success();
    }
}
