package com.shop.online.module.admin.controller;

import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.Result;
import com.shop.online.module.admin.dto.AdminProductQueryDTO;
import com.shop.online.module.admin.dto.CategorySaveDTO;
import com.shop.online.module.admin.dto.ProductSaveDTO;
import com.shop.online.module.admin.service.IAdminProductService;
import com.shop.online.module.product.vo.CategoryVO;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 — 商品管理控制器
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "管理端 — 商品管理")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final IAdminProductService adminProductService;

    @GetMapping
    @Operation(summary = "商品列表")
    public Result<PageResult<ProductVO>> listProducts(@Valid AdminProductQueryDTO dto) {
        return Result.success(adminProductService.pageProducts(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<ProductDetailVO> getProduct(@PathVariable Long id) {
        return Result.success(adminProductService.getProductDetail(id));
    }

    @PostMapping
    @Operation(summary = "新增商品")
    public Result<Long> createProduct(@Valid @RequestBody ProductSaveDTO dto) {
        return Result.success(adminProductService.saveProduct(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑商品")
    public Result<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductSaveDTO dto) {
        dto.setId(id);
        adminProductService.saveProduct(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "商品上下架")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminProductService.updateProductStatus(id, status);
        return Result.success();
    }
}
