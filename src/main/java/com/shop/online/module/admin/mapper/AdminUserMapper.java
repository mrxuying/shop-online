package com.shop.online.module.admin.mapper;

import com.shop.online.module.admin.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员 Mapper
 */
@Mapper
public interface AdminUserMapper {

    int insert(AdminUser adminUser);

    int updateById(AdminUser adminUser);

    int deleteById(@Param("id") Long id);

    AdminUser selectById(@Param("id") Long id);

    List<AdminUser> selectList(@Param("query") AdminUser query);

    Long selectCount(@Param("query") AdminUser query);

    AdminUser selectOne(@Param("query") AdminUser query);
}
