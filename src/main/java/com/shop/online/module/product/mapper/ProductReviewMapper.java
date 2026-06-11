package com.shop.online.module.product.mapper;

import com.shop.online.module.product.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品评价 Mapper
 */
@Mapper
public interface ProductReviewMapper {

    int insert(ProductReview review);

    int updateById(ProductReview review);

    int deleteById(@Param("id") Long id);

    ProductReview selectById(@Param("id") Long id);

    List<ProductReview> selectList(@Param("query") ProductReview query);

    Long selectCount(@Param("query") ProductReview query);

    ProductReview selectOne(@Param("query") ProductReview query);
}
