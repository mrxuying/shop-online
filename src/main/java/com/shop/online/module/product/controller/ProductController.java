package com.shop.online.module.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.Result;
import com.shop.online.module.product.dto.ProductQueryDTO;
import com.shop.online.module.product.dto.ProductSearchDTO;
import com.shop.online.module.product.service.IProductService;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductReviewVO;
import com.shop.online.module.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "商品管理")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    @Operation(summary = "商品列表（分页+筛选+排序）")
    public Result<PageResult<ProductVO>> listProducts(@Valid ProductQueryDTO dto) {
        IPage<ProductVO> page = productService.pageProducts(dto);
        return Result.success(PageResult.of(page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize(), page.getRecords()));
    }

    @GetMapping("/search")
    @Operation(summary = "商品搜索")
    public Result<PageResult<ProductVO>> searchProducts(@Valid ProductSearchDTO dto) {
        IPage<ProductVO> page = productService.searchProducts(dto);
        return Result.success(PageResult.of(page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize(), page.getRecords()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "商品评价列表")
    public Result<List<ProductReviewVO>> listReviews(@PathVariable Long id) {
        return Result.success(productService.listProductReviews(id));
    }
}
