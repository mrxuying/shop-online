package com.shop.online.module.product.converter;

import com.shop.online.module.product.entity.*;
import com.shop.online.module.product.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商品模块对象转换器
 */
@Mapper(componentModel = "spring")
public interface ProductConverter {

    ProductConverter INSTANCE = Mappers.getMapper(ProductConverter.class);

    /**
     * Product → ProductVO
     */
    ProductVO toVO(Product product);

    /**
     * List<Product> → List<ProductVO>
     */
    List<ProductVO> toVOList(List<Product> products);

    /**
     * Product → ProductDetailVO
     */
    @Mapping(target = "skus", ignore = true)
    @Mapping(target = "images", ignore = true)
    ProductDetailVO toDetailVO(Product product);

    /**
     * Category → CategoryVO
     */
    @Mapping(target = "children", ignore = true)
    CategoryVO toCategoryVO(Category category);

    /**
     * ProductSku → ProductSkuVO
     */
    ProductSkuVO toSkuVO(ProductSku sku);

    /**
     * List<ProductSku> → List<ProductSkuVO>
     */
    List<ProductSkuVO> toSkuVOList(List<ProductSku> skus);

    /**
     * ProductReview → ProductReviewVO
     */
    ProductReviewVO toReviewVO(ProductReview review);

    /**
     * List<ProductReview> → List<ProductReviewVO>
     */
    List<ProductReviewVO> toReviewVOList(List<ProductReview> reviews);
}
