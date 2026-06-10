package com.shop.online.module.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.product.converter.ProductConverter;
import com.shop.online.module.product.dto.ProductQueryDTO;
import com.shop.online.module.product.dto.ProductSearchDTO;
import com.shop.online.module.product.entity.*;
import com.shop.online.module.product.mapper.*;
import com.shop.online.module.product.service.IProductService;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductReviewVO;
import com.shop.online.module.product.vo.ProductSkuVO;
import com.shop.online.module.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final ProductImageMapper imageMapper;
    private final ProductReviewMapper reviewMapper;
    private final ProductConverter productConverter;

    @Override
    public IPage<ProductVO> pageProducts(ProductQueryDTO dto) {
        Page<Product> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        wrapper.eq(Objects.nonNull(dto.getCategoryId()), Product::getCategoryId, dto.getCategoryId());

        // 只查上架商品
        wrapper.eq(Product::getStatus, Objects.requireNonNullElse(dto.getStatus(), 1));

        // 排序
        String sortField = StrUtil.isNotBlank(dto.getSortField()) ? dto.getSortField() : "create_time";
        boolean isAsc = "asc".equalsIgnoreCase(dto.getSortOrder());

        wrapper.orderBy(true, isAsc, switch (sortField) {
            case "price" -> Product::getPrice;
            case "sales" -> Product::getSales;
            default -> Product::getCreateTime;
        });

        return productMapper.selectPage(page, wrapper).convert(productConverter::toVO);
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || Objects.equals(product.getIsDeleted(), 1)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (Objects.equals(product.getStatus(), 0)) {
            throw new BusinessException(ResultCode.PRODUCT_OFF_SHELF);
        }

        ProductDetailVO detailVO = productConverter.toDetailVO(product);

        // 查询SKU列表
        List<ProductSku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getProductId, id)
                        .eq(ProductSku::getStatus, 1));
        detailVO.setSkus(productConverter.toSkuVOList(skus));

        // 查询图片列表
        List<ProductImage> images = imageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, id)
                        .orderByAsc(ProductImage::getSortOrder));
        detailVO.setImages(images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));

        return detailVO;
    }

    @Override
    public IPage<ProductVO> searchProducts(ProductSearchDTO dto) {
        Page<Product> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（商品名称 LIKE 查询）
        wrapper.like(Product::getName, dto.getKeyword())
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales);

        return productMapper.selectPage(page, wrapper).convert(productConverter::toVO);
    }

    @Override
    public List<ProductReviewVO> listProductReviews(Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        List<ProductReview> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<ProductReview>()
                        .eq(ProductReview::getProductId, productId)
                        .eq(ProductReview::getIsHidden, 0)
                        .orderByDesc(ProductReview::getCreateTime));

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        return productConverter.toReviewVOList(reviews);
    }
}
