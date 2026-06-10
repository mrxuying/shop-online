package com.shop.online.module.admin.controller;

import com.shop.online.common.result.Result;
import com.shop.online.module.admin.dto.CategorySaveDTO;
import com.shop.online.module.admin.service.IAdminProductService;
import com.shop.online.module.product.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 — 分类管理控制器
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "管理端 — 分类管理")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final IAdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "分类树")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(adminProductService.getCategoryTree());
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Long> createCategory(@Valid @RequestBody CategorySaveDTO dto) {
        return Result.success(adminProductService.saveCategory(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑分类")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO dto) {
        dto.setId(id);
        adminProductService.saveCategory(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminProductService.deleteCategory(id);
        return Result.success();
    }
}
