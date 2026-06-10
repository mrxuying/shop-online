package com.shop.online.module.cart.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shop.online.common.constant.AppConstants;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.cart.dto.CartItemDTO;
import com.shop.online.module.cart.dto.CartSelectAllDTO;
import com.shop.online.module.cart.service.ICartService;
import com.shop.online.module.cart.vo.CartItemVO;
import com.shop.online.module.cart.vo.CartVO;
import com.shop.online.module.product.entity.Product;
import com.shop.online.module.product.entity.ProductSku;
import com.shop.online.module.product.mapper.ProductMapper;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 购物车服务实现 — Redis Hash 存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;

    @Override
    public CartVO getCart(Long userId) {
        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        List<Object> values = redisTemplate.opsForHash().values(cartKey);

        if (values == null || values.isEmpty()) {
            return CartVO.builder()
                    .items(Collections.emptyList())
                    .totalAmount(BigDecimal.ZERO)
                    .totalQuantity(0)
                    .allSelected(true)
                    .build();
        }

        List<CartItemVO> items = values.stream()
                .map(v -> JSONUtil.toBean(v.toString(), CartItemVO.class))
                .toList();

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        boolean allSelected = true;

        for (CartItemVO item : items) {
            if (Boolean.TRUE.equals(item.getSelected())) {
                totalAmount = totalAmount.add(item.getSubTotal());
                totalQuantity += item.getQuantity();
            } else {
                allSelected = false;
            }
        }

        return CartVO.builder()
                .items(items)
                .totalAmount(totalAmount)
                .totalQuantity(totalQuantity)
                .allSelected(allSelected)
                .build();
    }

    @Override
    public void addItem(Long userId, CartItemDTO dto) {
        // 校验商品和 SKU
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || Objects.equals(product.getIsDeleted(), 1)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (Objects.equals(product.getStatus(), 0)) {
            throw new BusinessException(ResultCode.PRODUCT_OFF_SHELF);
        }

        ProductSku sku = skuMapper.selectById(dto.getSkuId());
        if (sku == null || !sku.getProductId().equals(dto.getProductId())) {
            throw new BusinessException(ResultCode.SKU_NOT_FOUND);
        }
        if (sku.getStock() < dto.getQuantity()) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        String field = String.valueOf(dto.getSkuId());

        // 检查是否已存在
        Object existing = redisTemplate.opsForHash().get(cartKey, field);
        CartItemVO item;
        int totalQuantity = dto.getQuantity();

        if (existing != null) {
            item = JSONUtil.toBean(existing.toString(), CartItemVO.class);
            totalQuantity = item.getQuantity() + dto.getQuantity();
            if (totalQuantity > AppConstants.MAX_CART_ITEM_COUNT) {
                throw new BusinessException(ResultCode.CART_ITEM_LIMIT_EXCEEDED);
            }
        }

        // 构建购物车项
        BigDecimal subTotal = sku.getPrice().multiply(BigDecimal.valueOf(totalQuantity));
        item = CartItemVO.builder()
                .productId(product.getId())
                .skuId(sku.getId())
                .productName(product.getName())
                .productImage(sku.getImage() != null ? sku.getImage() : product.getMainImage())
                .specInfo(sku.getSpecInfo())
                .price(sku.getPrice())
                .quantity(totalQuantity)
                .subTotal(subTotal)
                .selected(Boolean.TRUE.equals(dto.getSelected()))
                .stock(sku.getStock())
                .build();

        redisTemplate.opsForHash().put(cartKey, field, JSONUtil.toJsonStr(item));
        redisTemplate.expire(cartKey, 30, TimeUnit.DAYS);

        log.info("添加到购物车成功, userId={}, skuId={}, quantity={}", userId, dto.getSkuId(), totalQuantity);
    }

    @Override
    public void updateItem(Long userId, Long skuId, Integer quantity) {
        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        String field = String.valueOf(skuId);

        Object existing = redisTemplate.opsForHash().get(cartKey, field);
        if (existing == null) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }

        CartItemVO item = JSONUtil.toBean(existing.toString(), CartItemVO.class);

        // 校验库存
        ProductSku sku = skuMapper.selectById(skuId);
        if (sku != null && sku.getStock() < quantity) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        item.setQuantity(quantity);
        item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));

        redisTemplate.opsForHash().put(cartKey, field, JSONUtil.toJsonStr(item));
        log.info("购物车数量更新成功, userId={}, skuId={}, quantity={}", userId, skuId, quantity);
    }

    @Override
    public void removeItem(Long userId, Long skuId) {
        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        String field = String.valueOf(skuId);

        Long deleted = redisTemplate.opsForHash().delete(cartKey, field);
        if (deleted == null || deleted == 0) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }
        log.info("购物车商品删除成功, userId={}, skuId={}", userId, skuId);
    }

    @Override
    public void selectAll(Long userId, CartSelectAllDTO dto) {
        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cartKey);

        if (entries.isEmpty()) {
            return;
        }

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            CartItemVO item = JSONUtil.toBean(entry.getValue().toString(), CartItemVO.class);
            item.setSelected(dto.getSelected());
            redisTemplate.opsForHash().put(cartKey, entry.getKey(), JSONUtil.toJsonStr(item));
        }

        log.info("购物车全选成功, userId={}, selected={}", userId, dto.getSelected());
    }

    @Override
    public void clearSelected(Long userId) {
        String cartKey = AppConstants.CART_REDIS_KEY_PREFIX + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cartKey);

        if (entries.isEmpty()) {
            return;
        }

        List<Object> toRemove = entries.entrySet().stream()
                .filter(e -> {
                    CartItemVO item = JSONUtil.toBean(e.getValue().toString(), CartItemVO.class);
                    return Boolean.TRUE.equals(item.getSelected());
                })
                .map(Map.Entry::getKey)
                .toList();

        if (!toRemove.isEmpty()) {
            redisTemplate.opsForHash().delete(cartKey, toRemove.toArray());
        }
        log.info("购物车已选商品清除成功, userId={}, count={}", userId, toRemove.size());
    }
}
