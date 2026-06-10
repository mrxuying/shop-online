package com.shop.online.module.admin.controller;

import com.shop.online.common.result.Result;
import com.shop.online.module.admin.dto.AdminLoginDTO;
import com.shop.online.module.admin.service.IAdminUserService;
import com.shop.online.module.admin.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理端 — 认证")
public class AdminAuthController {

    private final IAdminUserService adminUserService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        return Result.success(adminUserService.login(dto));
    }
}
