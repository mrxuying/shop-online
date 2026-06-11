package com.shop.online.module.admin.service;

import com.shop.online.common.result.PageResult;
import com.shop.online.module.admin.dto.AdminProductQueryDTO;
import com.shop.online.module.admin.dto.CategorySaveDTO;
import com.shop.online.module.admin.dto.ProductSaveDTO;
import com.shop.online.module.product.vo.CategoryVO;
import com.shop.online.module.product.vo.ProductDetailVO;
import com.shop.online.module.product.vo.ProductVO;

import java.util.List;

/**
 * 管理端商品服务接口
 */
public interface IAdminProductService {

    // 商品管理
    PageResult<ProductVO> pageProducts(AdminProductQueryDTO dto);

    ProductDetailVO getProductDetail(Long id);

    Long saveProduct(ProductSaveDTO dto);

    void deleteProduct(Long id);

    void updateProductStatus(Long id, Integer status);

    // 分类管理
    List<CategoryVO> getCategoryTree();

    Long saveCategory(CategorySaveDTO dto);

    void deleteCategory(Long id);
}
