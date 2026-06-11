package com.shop.online.module.user.mapper;

import com.shop.online.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    int updateById(User user);

    int deleteById(@Param("id") Long id);

    User selectById(@Param("id") Long id);

    List<User> selectList(@Param("query") User query);

    Long selectCount(@Param("query") User query);

    User selectOne(@Param("query") User query);

    /**
     * 用户名或手机号查询（用于登录）
     */
    User selectByUsernameOrPhone(@Param("usernameOrPhone") String usernameOrPhone);

    /**
     * 关键词模糊搜索（用户名或手机号）
     */
    List<User> selectByKeyword(@Param("keyword") String keyword);
}
