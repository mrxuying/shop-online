package com.shop.online.module.product.controller;

import com.shop.online.common.result.Result;
import com.shop.online.module.product.service.ICategoryService;
import com.shop.online.module.product.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "商品分类")
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取分类树")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }
}
