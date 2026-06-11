package com.shop.online.module.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
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
    public PageResult<ProductVO> pageProducts(ProductQueryDTO dto) {
        Product query = new Product();
        query.setCategoryId(dto.getCategoryId());
        query.setStatus(Objects.requireNonNullElse(dto.getStatus(), 1));

        // 排序
        String sortField = StrUtil.isNotBlank(dto.getSortField()) ? dto.getSortField() : "create_time";
        String sortOrder = "asc".equalsIgnoreCase(dto.getSortOrder()) ? "asc" : "desc";
        String orderByColumn;
        switch (sortField) {
            case "price":
                orderByColumn = "price";
                break;
            case "sales":
                orderByColumn = "sales";
                break;
            default:
                orderByColumn = "create_time";
                break;
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageHelper.orderBy(orderByColumn + " " + sortOrder);

        List<Product> list = productMapper.selectList(query);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        List<ProductVO> records = list.stream().map(productConverter::toVO).toList();
        return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
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
        ProductSku skuQuery = new ProductSku();
        skuQuery.setProductId(id);
        skuQuery.setStatus(1);
        List<ProductSku> skus = skuMapper.selectList(skuQuery);
        detailVO.setSkus(productConverter.toSkuVOList(skus));

        // 查询图片列表
        ProductImage imageQuery = new ProductImage();
        imageQuery.setProductId(id);
        List<ProductImage> images = imageMapper.selectList(imageQuery);
        detailVO.setImages(images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));

        return detailVO;
    }

    @Override
    public PageResult<ProductVO> searchProducts(ProductSearchDTO dto) {
        Product query = new Product();
        query.setName(dto.getKeyword());
        query.setStatus(1);

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageHelper.orderBy("sales desc");

        List<Product> list = productMapper.selectList(query);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        List<ProductVO> records = list.stream().map(productConverter::toVO).toList();
        return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
    }

    @Override
    public List<ProductReviewVO> listProductReviews(Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        ProductReview query = new ProductReview();
        query.setProductId(productId);
        query.setIsHidden(0);
        List<ProductReview> reviews = reviewMapper.selectList(query);

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        return productConverter.toReviewVOList(reviews);
    }
}
