package com.shop.online.module.product;

import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.product.converter.ProductConverter;
import com.shop.online.module.product.dto.ProductQueryDTO;
import com.shop.online.module.product.dto.ProductSearchDTO;
import com.shop.online.module.product.entity.*;
import com.shop.online.module.product.mapper.*;
import com.shop.online.module.product.service.impl.ProductServiceImpl;
import com.shop.online.module.product.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("商品服务测试")
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductSkuMapper skuMapper;

    @Mock
    private ProductImageMapper imageMapper;

    @Mock
    private ProductReviewMapper reviewMapper;

    @Mock
    private ProductConverter productConverter;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductSku testSku;
    private ProductImage testImage;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setCategoryId(7L);
        testProduct.setName("iPhone 15 Pro Max");
        testProduct.setSubtitle("A17 Pro 芯片");
        testProduct.setMainImage("https://example.com/iphone.jpg");
        testProduct.setPrice(new BigDecimal("8999.00"));
        testProduct.setSales(2580);
        testProduct.setStatus(1);
        testProduct.setIsDeleted(0);
        testProduct.setCreateTime(LocalDateTime.now());

        testSku = new ProductSku();
        testSku.setId(1L);
        testSku.setProductId(1L);
        testSku.setSkuCode("IP15PM-256-BLK");
        testSku.setSpecInfo("{\"颜色\":\"黑色钛金属\",\"容量\":\"256GB\"}");
        testSku.setPrice(new BigDecimal("8999.00"));
        testSku.setStock(100);
        testSku.setStatus(1);

        testImage = new ProductImage();
        testImage.setId(1L);
        testImage.setProductId(1L);
        testImage.setImageUrl("https://example.com/iphone-1.jpg");
        testImage.setSortOrder(1);
    }

    // ==================== 商品列表 ====================

    @Test
    @DisplayName("商品分页列表 — 默认排序")
    void shouldPageProducts() {
        // Given
        ProductQueryDTO dto = new ProductQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);

        ProductVO productVO = new ProductVO();
        productVO.setId(1L);
        productVO.setName("iPhone 15 Pro Max");

        when(productMapper.selectList(any(Product.class)))
                .thenReturn(Arrays.asList(testProduct));
        when(productConverter.toVO(testProduct)).thenReturn(productVO);

        // When
        PageResult<ProductVO> result = productService.pageProducts(dto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("iPhone 15 Pro Max", result.getRecords().get(0).getName());
        verify(productMapper).selectList(any(Product.class));
    }

    @Test
    @DisplayName("商品分页列表 — 按分类筛选")
    void shouldPageProductsByCategory() {
        // Given
        ProductQueryDTO dto = new ProductQueryDTO();
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setCategoryId(7L);

        ProductVO productVO = new ProductVO();
        productVO.setId(1L);
        productVO.setName("iPhone 15 Pro Max");

        when(productMapper.selectList(any(Product.class)))
                .thenReturn(Arrays.asList(testProduct));
        when(productConverter.toVO(testProduct)).thenReturn(productVO);

        // When
        PageResult<ProductVO> result = productService.pageProducts(dto);

        // Then
        assertNotNull(result);
        verify(productMapper).selectList(any(Product.class));
    }

    // ==================== 商品详情 ====================

    @Test
    @DisplayName("商品详情 — 成功（含SKU和图片）")
    void shouldGetProductDetail() {
        // Given
        ProductDetailVO detailVO = new ProductDetailVO();
        detailVO.setId(1L);
        detailVO.setName("iPhone 15 Pro Max");

        ProductSkuVO skuVO = new ProductSkuVO();
        skuVO.setId(1L);
        skuVO.setSkuCode("IP15PM-256-BLK");

        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(skuMapper.selectList(any(ProductSku.class)))
                .thenReturn(Arrays.asList(testSku));
        when(imageMapper.selectList(any(ProductImage.class)))
                .thenReturn(Arrays.asList(testImage));
        when(productConverter.toDetailVO(testProduct)).thenReturn(detailVO);
        when(productConverter.toSkuVOList(any())).thenReturn(Arrays.asList(skuVO));

        // When
        ProductDetailVO result = productService.getProductDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals("iPhone 15 Pro Max", result.getName());
        assertNotNull(result.getSkus());
        assertEquals(1, result.getSkus().size());
        assertEquals("IP15PM-256-BLK", result.getSkus().get(0).getSkuCode());
        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());
        verify(productMapper).selectById(1L);
        verify(skuMapper).selectList(any(ProductSku.class));
        verify(imageMapper).selectList(any(ProductImage.class));
    }

    @Test
    @DisplayName("商品详情 — 商品不存在")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.getProductDetail(999L));
        assertEquals(ResultCode.PRODUCT_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("商品详情 — 商品已下架")
    void shouldThrowExceptionWhenProductOffShelf() {
        // Given
        testProduct.setStatus(0);
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> productService.getProductDetail(1L));
        assertEquals(ResultCode.PRODUCT_OFF_SHELF.getCode(), ex.getCode());
    }

    // ==================== 商品搜索 ====================

    @Test
    @DisplayName("商品搜索 — 关键词搜索成功")
    void shouldSearchProducts() {
        // Given
        ProductSearchDTO dto = new ProductSearchDTO();
        dto.setKeyword("iPhone");
        dto.setPageNum(1);
        dto.setPageSize(10);

        ProductVO productVO = new ProductVO();
        productVO.setId(1L);
        productVO.setName("iPhone 15 Pro Max");

        when(productMapper.selectList(any(Product.class)))
                .thenReturn(Arrays.asList(testProduct));
        when(productConverter.toVO(testProduct)).thenReturn(productVO);

        // When
        PageResult<ProductVO> result = productService.searchProducts(dto);

        // Then
        assertNotNull(result);
        verify(productMapper).selectList(any(Product.class));
    }

    @Test
    @DisplayName("商品搜索 — 无结果")
    void shouldReturnEmptyWhenSearchNoMatch() {
        // Given
        ProductSearchDTO dto = new ProductSearchDTO();
        dto.setKeyword("不存在的商品");
        dto.setPageNum(1);
        dto.setPageSize(10);

        when(productMapper.selectList(any(Product.class)))
                .thenReturn(List.of());

        // When
        PageResult<ProductVO> result = productService.searchProducts(dto);

        // Then
        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
    }

    // ==================== 商品评价 ====================

    @Test
    @DisplayName("商品评价列表 — 成功")
    void shouldListProductReviews() {
        // Given
        ProductReview review = new ProductReview();
        review.setId(1L);
        review.setProductId(1L);
        review.setUserId(1L);
        review.setRating(5);
        review.setContent("质量很好，非常满意");
        review.setIsHidden(0);
        review.setCreateTime(LocalDateTime.now());

        ProductReviewVO reviewVO = new ProductReviewVO();
        reviewVO.setId(1L);
        reviewVO.setRating(5);
        reviewVO.setContent("质量很好，非常满意");

        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(reviewMapper.selectList(any(ProductReview.class)))
                .thenReturn(Arrays.asList(review));
        when(productConverter.toReviewVOList(any())).thenReturn(Arrays.asList(reviewVO));

        // When
        List<ProductReviewVO> result = productService.listProductReviews(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
        assertEquals("质量很好，非常满意", result.get(0).getContent());
        verify(productMapper).selectById(1L);
        verify(reviewMapper).selectList(any(ProductReview.class));
    }

    @Test
    @DisplayName("商品评价列表 — 商品不存在")
    void shouldThrowExceptionWhenProductNotFoundForReviews() {
        // Given
        when(productMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> productService.listProductReviews(999L));
    }
}
