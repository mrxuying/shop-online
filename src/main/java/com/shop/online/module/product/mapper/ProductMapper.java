package com.shop.online.module.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.online.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
