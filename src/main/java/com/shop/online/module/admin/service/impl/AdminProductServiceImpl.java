package com.shop.online.module.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.admin.dto.AdminProductQueryDTO;
import com.shop.online.module.admin.dto.CategorySaveDTO;
import com.shop.online.module.admin.dto.ProductSaveDTO;
import com.shop.online.module.admin.service.IAdminProductService;
import com.shop.online.module.product.converter.ProductConverter;
import com.shop.online.module.product.entity.Category;
import com.shop.online.module.product.entity.Product;
import com.shop.online.module.product.entity.ProductImage;
import com.shop.online.module.product.entity.ProductSku;
import com.shop.online.module.product.mapper.CategoryMapper;
import com.shop.online.module.product.mapper.ProductImageMapper;
import com.shop.online.module.product.mapper.ProductMapper;
import com.shop.online.module.product.mapper.ProductSkuMapper;
import com.shop.online.module.product.vo.CategoryVO;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements IAdminProductService {

    private final ProductMapper productMapper;
    private final ProductSkuMapper skuMapper;
    private final ProductImageMapper imageMapper;
    private final CategoryMapper categoryMapper;
    private final ProductConverter productConverter;

    // ==================== 商品管理 ====================

    @Override
    public PageResult<ProductVO> pageProducts(AdminProductQueryDTO dto) {
        Product query = new Product();
        if (dto.getCategoryId() != null) {
            query.setCategoryId(dto.getCategoryId());
        }
        if (dto.getStatus() != null) {
            query.setStatus(dto.getStatus());
        }
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            query.setName(dto.getKeyword());
        }

        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageHelper.orderBy("create_time desc");

        List<Product> list = productMapper.selectList(query);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        List<ProductVO> records = list.stream().map(productConverter::toVO).toList();
        return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        ProductDetailVO detailVO = productConverter.toDetailVO(product);

        ProductSku skuQuery = new ProductSku();
        skuQuery.setProductId(id);
        List<ProductSku> skus = skuMapper.selectList(skuQuery);
        detailVO.setSkus(productConverter.toSkuVOList(skus));

        ProductImage imageQuery = new ProductImage();
        imageQuery.setProductId(id);
        List<ProductImage> images = imageMapper.selectList(imageQuery);
        detailVO.setImages(images.stream().map(ProductImage::getImageUrl).toList());

        return detailVO;
    }

    @Override
    @Transactional
    public Long saveProduct(ProductSaveDTO dto) {
        Product product;
        LocalDateTime now = LocalDateTime.now();
        if (dto.getId() != null) {
            // 编辑
            product = productMapper.selectById(dto.getId());
            if (product == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
            }
        } else {
            // 新增
            product = new Product();
            product.setCreateTime(now);
            product.setIsDeleted(0);
        }

        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setSubtitle(dto.getSubtitle());
        product.setMainImage(dto.getMainImage());
        product.setDetail(dto.getDetail());
        product.setPrice(dto.getPrice());
        product.setStatus(Objects.requireNonNullElse(dto.getStatus(), 1));
        product.setUpdateTime(now);

        if (dto.getId() != null) {
            productMapper.updateById(product);
            log.info("商品更新成功, productId={}", dto.getId());
        } else {
            productMapper.insert(product);
            log.info("商品新增成功, productId={}", product.getId());
        }

        return product.getId();
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        productMapper.deleteById(id);
        log.info("商品删除成功, productId={}", id);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("商品状态更新成功, productId={}, status={}", id, status);
    }

    // ==================== 分类管理 ====================

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> categories = categoryMapper.selectList(null);
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        List<CategoryVO> allVos = categories.stream()
                .map(productConverter::toCategoryVO)
                .toList();

        Map<Long, List<CategoryVO>> parentMap = allVos.stream()
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        List<CategoryVO> roots = parentMap.getOrDefault(0L, Collections.emptyList());
        for (CategoryVO root : roots) {
            setChildren(root, parentMap);
        }
        return roots;
    }

    private void setChildren(CategoryVO parent, Map<Long, List<CategoryVO>> parentMap) {
        List<CategoryVO> children = parentMap.getOrDefault(parent.getId(), Collections.emptyList());
        parent.setChildren(children);
        for (CategoryVO child : children) {
            setChildren(child, parentMap);
        }
    }

    @Override
    @Transactional
    public Long saveCategory(CategorySaveDTO dto) {
        Category category;
        LocalDateTime now = LocalDateTime.now();
        if (dto.getId() != null) {
            category = categoryMapper.selectById(dto.getId());
            if (category == null) {
                throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
            }
        } else {
            category = new Category();
            category.setCreateTime(now);
        }

        category.setParentId(Objects.requireNonNullElse(dto.getParentId(), 0L));
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSortOrder(Objects.requireNonNullElse(dto.getSortOrder(), 0));
        category.setStatus(Objects.requireNonNullElse(dto.getStatus(), 1));
        category.setUpdateTime(now);

        if (dto.getId() != null) {
            categoryMapper.updateById(category);
            log.info("分类更新成功, categoryId={}", dto.getId());
        } else {
            categoryMapper.insert(category);
            log.info("分类新增成功, categoryId={}", category.getId());
        }

        return category.getId();
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 检查是否有子分类
        Category childQuery = new Category();
        childQuery.setParentId(id);
        Long childCount = categoryMapper.selectCount(childQuery);
        if (childCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_CHILDREN);
        }

        // 检查是否有商品
        Product productQuery = new Product();
        productQuery.setCategoryId(id);
        Long productCount = productMapper.selectCount(productQuery);
        if (productCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_PRODUCTS);
        }

        categoryMapper.deleteById(id);
        log.info("分类删除成功, categoryId={}", id);
    }
}
