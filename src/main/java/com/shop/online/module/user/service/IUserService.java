package com.shop.online.module.user.service;

import com.shop.online.module.user.dto.*;
import com.shop.online.module.user.vo.UserAddressVO;
import com.shop.online.module.user.vo.UserLoginVO;
import com.shop.online.module.user.vo.UserProfileVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 用户注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录（支持用户名/手机号）
     */
    UserLoginVO login(UserLoginDTO dto);

    /**
     * 刷新 Token
     */
    UserLoginVO refreshToken(RefreshTokenDTO dto);

    /**
     * 获取个人信息
     */
    UserProfileVO getProfile(Long userId);

    /**
     * 修改个人信息
     */
    void updateProfile(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordUpdateDTO dto);

    /**
     * 获取收货地址列表
     */
    List<UserAddressVO> listAddresses(Long userId);

    /**
     * 新增收货地址
     */
    Long createAddress(Long userId, UserAddressDTO dto);

    /**
     * 修改收货地址
     */
    void updateAddress(Long userId, Long addressId, UserAddressDTO dto);

    /**
     * 删除收货地址
     */
    void deleteAddress(Long userId, Long addressId);
}
