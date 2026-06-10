package com.shop.online.module.product.service.impl;

import com.shop.online.module.product.converter.ProductConverter;
import com.shop.online.module.product.entity.Category;
import com.shop.online.module.product.mapper.CategoryMapper;
import com.shop.online.module.product.service.ICategoryService;
import com.shop.online.module.product.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductConverter productConverter;

    @Override
    @Cacheable(value = "categoryTree", key = "'all'")
    public List<CategoryVO> getCategoryTree() {
        List<Category> categories = categoryMapper.selectList(null);
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为 VO
        List<CategoryVO> allVos = categories.stream()
                .map(productConverter::toCategoryVO)
                .toList();

        // 按 parentId 分组
        Map<Long, List<CategoryVO>> parentMap = allVos.stream()
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        // 设置子分类（一级分类 parentId = 0）
        List<CategoryVO> rootCategories = parentMap.getOrDefault(0L, Collections.emptyList());
        for (CategoryVO root : rootCategories) {
            setChildren(root, parentMap);
        }

        return rootCategories;
    }

    /**
     * 递归设置子分类
     */
    private void setChildren(CategoryVO parent, Map<Long, List<CategoryVO>> parentMap) {
        List<CategoryVO> children = parentMap.getOrDefault(parent.getId(), Collections.emptyList());
        parent.setChildren(children);
        for (CategoryVO child : children) {
            setChildren(child, parentMap);
        }
    }
}
