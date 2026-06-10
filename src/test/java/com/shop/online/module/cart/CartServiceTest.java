package com.shop.online.module.cart;

import cn.hutool.json.JSONUtil;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.module.cart.dto.CartItemDTO;
import com.shop.online.module.cart.dto.CartSelectAllDTO;
import com.shop.online.module.cart.service.impl.CartServiceImpl;
import com.shop.online.module.cart.vo.CartItemVO;
import com.shop.online.module.cart.vo.CartVO;
import com.shop.online.module.product.entity.Product;
import com.shop.online.module.product.entity.ProductSku;
import com.shop.online.module.product.mapper.ProductMapper;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("购物车服务测试")
class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductSkuMapper skuMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Product testProduct;
    private ProductSku testSku;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("iPhone 15 Pro Max");
        testProduct.setMainImage("https://example.com/iphone.jpg");
        testProduct.setPrice(new BigDecimal("8999.00"));
        testProduct.setStatus(1);
        testProduct.setIsDeleted(0);

        testSku = new ProductSku();
        testSku.setId(1L);
        testSku.setProductId(1L);
        testSku.setSkuCode("IP15PM-256-BLK");
        testSku.setSpecInfo("{\"颜色\":\"黑色钛金属\",\"容量\":\"256GB\"}");
        testSku.setPrice(new BigDecimal("8999.00"));
        testSku.setStock(100);

        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("获取购物车 — 空购物车")
    void shouldReturnEmptyCart() {
        // Given
        when(hashOperations.values(anyString())).thenReturn(List.of());

        // When
        CartVO result = cartService.getCart(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    @DisplayName("添加商品到购物车 — 成功")
    void shouldAddItemToCart() {
        // Given
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(1L);
        dto.setSkuId(1L);
        dto.setQuantity(2);
        dto.setSelected(true);

        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(skuMapper.selectById(1L)).thenReturn(testSku);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        // When
        assertDoesNotThrow(() -> cartService.addItem(1L, dto));

        // Then
        verify(hashOperations).put(anyString(), eq("1"), anyString());
        verify(redisTemplate).expire(anyString(), eq(30L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("添加商品到购物车 — 商品已下架")
    void shouldThrowExceptionWhenProductOffShelf() {
        // Given
        testProduct.setStatus(0);
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(1L);
        dto.setSkuId(1L);
        dto.setQuantity(1);

        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // When & Then
        assertThrows(BusinessException.class, () -> cartService.addItem(1L, dto));
    }

    @Test
    @DisplayName("删除购物车商品 — 成功")
    void shouldRemoveCartItem() {
        // Given
        when(hashOperations.delete(anyString(), eq("1"))).thenReturn(1L);

        // When
        assertDoesNotThrow(() -> cartService.removeItem(1L, 1L));

        // Then
        verify(hashOperations).delete(anyString(), eq("1"));
    }

    @Test
    @DisplayName("全选 — 成功")
    void shouldSelectAll() {
        // Given
        CartItemVO item = CartItemVO.builder()
                .skuId(1L)
                .selected(false)
                .quantity(1)
                .price(new BigDecimal("8999.00"))
                .subTotal(new BigDecimal("8999.00"))
                .build();

        CartSelectAllDTO dto = new CartSelectAllDTO();
        dto.setSelected(true);

        when(hashOperations.entries(anyString()))
                .thenReturn(Map.of("1", JSONUtil.toJsonStr(item)));

        // When
        assertDoesNotThrow(() -> cartService.selectAll(1L, dto));

        // Then
        verify(hashOperations).put(anyString(), eq("1"), anyString());
    }
}
