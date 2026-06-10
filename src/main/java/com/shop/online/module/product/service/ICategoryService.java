package com.shop.online.module.product.service;

import com.shop.online.module.product.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface ICategoryService {

    /**
     * 获取分类树
     */
    List<CategoryVO> getCategoryTree();
}
