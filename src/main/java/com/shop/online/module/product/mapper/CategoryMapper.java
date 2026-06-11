package com.shop.online.module.product.mapper;

import com.shop.online.module.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类 Mapper
 */
@Mapper
public interface CategoryMapper {

    int insert(Category category);

    int updateById(Category category);

    int deleteById(@Param("id") Long id);

    Category selectById(@Param("id") Long id);

    List<Category> selectList(@Param("query") Category query);

    Long selectCount(@Param("query") Category query);

    Category selectOne(@Param("query") Category query);
}
