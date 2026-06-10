package com.shop.online.module.product.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.online.module.product.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 扣减库存（乐观锁：stock >= quantity）
     *
     * @param skuId    SKU ID
     * @param quantity 扣减数量
     * @return 影响行数（0 = 库存不足）
     */
    default int updateStock(Long skuId, Integer quantity) {
        LambdaUpdateWrapper<ProductSku> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock = stock - " + quantity)
                .eq(ProductSku::getId, skuId)
                .ge(ProductSku::getStock, quantity);
        return update(null, wrapper);
    }

    /**
     * 释放库存（订单取消/退款时）
     *
     * @param skuId    SKU ID
     * @param quantity 释放数量
     * @return 影响行数
     */
    default int releaseStock(Long skuId, Integer quantity) {
        LambdaUpdateWrapper<ProductSku> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("stock = stock + " + quantity)
                .eq(ProductSku::getId, skuId);
        return update(null, wrapper);
    }
}
