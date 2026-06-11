package com.shop.online.module.admin.controller;

import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.Result;
import com.shop.online.module.admin.dto.AdminUserQueryDTO;
import com.shop.online.module.admin.service.IAdminUserManageService;
import com.shop.online.module.user.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 — 用户管理控制器
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "管理端 — 用户管理")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final IAdminUserManageService adminUserManageService;

    @GetMapping
    @Operation(summary = "用户列表")
    public Result<PageResult<UserProfileVO>> listUsers(@Valid AdminUserQueryDTO dto) {
        return Result.success(adminUserManageService.pageUsers(dto));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "用户状态变更")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminUserManageService.updateUserStatus(id, status);
        return Result.success();
    }
}
