package com.shop.online.module.cart.service;

import com.shop.online.module.cart.dto.CartItemDTO;
import com.shop.online.module.cart.dto.CartSelectAllDTO;
import com.shop.online.module.cart.vo.CartVO;

/**
 * 购物车服务接口
 */
public interface ICartService {

    /**
     * 获取购物车
     */
    CartVO getCart(Long userId);

    /**
     * 添加商品到购物车
     */
    void addItem(Long userId, CartItemDTO dto);

    /**
     * 修改购物车商品数量
     */
    void updateItem(Long userId, Long skuId, Integer quantity);

    /**
     * 删除购物车商品
     */
    void removeItem(Long userId, Long skuId);

    /**
     * 全选/取消全选
     */
    void selectAll(Long userId, CartSelectAllDTO dto);

    /**
     * 清空购物车（下单后清除已选商品）
     */
    void clearSelected(Long userId);
}
