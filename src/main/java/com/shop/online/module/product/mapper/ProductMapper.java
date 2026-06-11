package com.shop.online.module.product.mapper;

import com.shop.online.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper {

    int insert(Product product);

    int updateById(Product product);

    int deleteById(@Param("id") Long id);

    Product selectById(@Param("id") Long id);

    List<Product> selectList(@Param("query") Product query);

    Long selectCount(@Param("query") Product query);

    Product selectOne(@Param("query") Product query);
}
