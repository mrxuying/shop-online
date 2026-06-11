package com.shop.online.module.product.mapper;

import com.shop.online.module.product.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品图片 Mapper
 */
@Mapper
public interface ProductImageMapper {

    int insert(ProductImage image);

    int updateById(ProductImage image);

    int deleteById(@Param("id") Long id);

    ProductImage selectById(@Param("id") Long id);

    List<ProductImage> selectList(@Param("query") ProductImage query);

    Long selectCount(@Param("query") ProductImage query);

    ProductImage selectOne(@Param("query") ProductImage query);
}
