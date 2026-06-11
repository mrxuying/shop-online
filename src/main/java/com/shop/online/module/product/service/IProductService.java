package com.shop.online.module.product.service;

import com.shop.online.common.result.PageResult;
import com.shop.online.module.product.dto.ProductQueryDTO;
import com.shop.online.module.product.dto.ProductSearchDTO;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductReviewVO;
import com.shop.online.module.product.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface IProductService {

    /**
     * 商品分页列表
     */
    PageResult<ProductVO> pageProducts(ProductQueryDTO dto);

    /**
     * 商品详情（含SKU+图片）
     */
    ProductDetailVO getProductDetail(Long id);

    /**
     * 商品搜索
     */
    PageResult<ProductVO> searchProducts(ProductSearchDTO dto);

    /**
     * 商品评价列表
     */
    List<ProductReviewVO> listProductReviews(Long productId);
}
