package com.shop.online.module.user.mapper;

import com.shop.online.module.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收货地址 Mapper
 */
@Mapper
public interface UserAddressMapper {

    int insert(UserAddress address);

    int updateById(UserAddress address);

    int deleteById(@Param("id") Long id);

    UserAddress selectById(@Param("id") Long id);

    List<UserAddress> selectList(@Param("query") UserAddress query);

    Long selectCount(@Param("query") UserAddress query);

    UserAddress selectOne(@Param("query") UserAddress query);
}
