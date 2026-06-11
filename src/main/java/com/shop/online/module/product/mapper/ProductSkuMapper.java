package com.shop.online.module.product.mapper;

import com.shop.online.module.product.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper {

    int insert(ProductSku sku);

    int updateById(ProductSku sku);

    int deleteById(@Param("id") Long id);

    ProductSku selectById(@Param("id") Long id);

    List<ProductSku> selectList(@Param("query") ProductSku query);

    Long selectCount(@Param("query") ProductSku query);

    ProductSku selectOne(@Param("query") ProductSku query);

    /**
     * 扣减库存（乐观锁：stock >= quantity）
     */
    int updateStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 释放库存（订单取消/退款时）
     */
    int releaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}
