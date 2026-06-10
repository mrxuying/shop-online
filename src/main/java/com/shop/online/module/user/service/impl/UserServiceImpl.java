package com.shop.online.module.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.online.common.constant.AppConstants;
import com.shop.online.common.enums.UserStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.infrastructure.security.JwtUtils;
import com.shop.online.module.user.converter.UserConverter;
import com.shop.online.module.user.dto.*;
import com.shop.online.module.user.entity.User;
import com.shop.online.module.user.entity.UserAddress;
import com.shop.online.module.user.mapper.UserAddressMapper;
import com.shop.online.module.user.mapper.UserMapper;
import com.shop.online.module.user.service.IUserService;
import com.shop.online.module.user.vo.UserAddressVO;
import com.shop.online.module.user.vo.UserLoginVO;
import com.shop.online.module.user.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final UserAddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserConverter userConverter;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void register(UserRegisterDTO dto) {
        // 校验用户名唯一性
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }

        // 校验手机号唯一性
        count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (count > 0) {
            throw new BusinessException(ResultCode.PHONE_EXIST);
        }

        // 构建用户实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getUsername());
        user.setStatus(UserStatusEnum.ENABLED.getCode());

        userMapper.insert(user);
        log.info("用户注册成功, userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        // 支持用户名或手机号登录
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .and(w -> w.eq(User::getUsername, dto.getUsername())
                                .or()
                                .eq(User::getPhone, dto.getUsername()))
                        .last("LIMIT 1"));

        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 校验账号状态
        if (Objects.equals(user.getStatus(), UserStatusEnum.DISABLED.getCode())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成 Token
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // Refresh Token 存入 Redis
        String redisKey = AppConstants.REFRESH_TOKEN_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(redisKey, refreshToken,
                Duration.ofSeconds(604800));

        log.info("用户登录成功, userId={}, username={}", user.getId(), user.getUsername());

        return UserLoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UserLoginVO refreshToken(RefreshTokenDTO dto) {
        // 解析 Refresh Token 获取用户 ID
        Long userId;
        try {
            userId = jwtUtils.getUserId(dto.getRefreshToken());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 校验 Redis 中的 Refresh Token
        String redisKey = AppConstants.REFRESH_TOKEN_KEY_PREFIX + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isBlank(storedToken) || !storedToken.equals(dto.getRefreshToken())) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 生成新 Token
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());

        // 更新 Redis 中的 Refresh Token
        redisTemplate.opsForValue().set(redisKey, refreshToken,
                Duration.ofSeconds(604800));

        return UserLoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userConverter.toProfileVO(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        userMapper.updateById(user);
        log.info("用户信息更新成功, userId={}", userId);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验原密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);

        // 清除 Redis 中的 Refresh Token，强制重新登录
        String redisKey = AppConstants.REFRESH_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);

        log.info("密码修改成功, userId={}", userId);
    }

    @Override
    public List<UserAddressVO> listAddresses(Long userId) {
        List<UserAddress> addresses = addressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreateTime));
        return userConverter.toAddressVOList(addresses);
    }

    @Override
    @Transactional
    public Long createAddress(Long userId, UserAddressDTO dto) {
        // 检查地址数量上限
        Long count = addressMapper.selectCount(
                new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        if (count >= AppConstants.MAX_ADDRESS_COUNT) {
            throw new BusinessException(ResultCode.ADDRESS_LIMIT_EXCEEDED);
        }

        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(Objects.equals(dto.getIsDefault(), 1) ? 1 : 0);

        // 如果设为默认地址，取消其他默认
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }

        addressMapper.insert(address);
        log.info("收货地址新增成功, userId={}, addressId={}", userId, address.getId());
        return address.getId();
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Long addressId, UserAddressDTO dto) {
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_FOUND);
        }

        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(Objects.equals(dto.getIsDefault(), 1) ? 1 : 0);

        // 如果设为默认地址，取消其他默认
        if (address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }

        addressMapper.updateById(address);
        log.info("收货地址更新成功, userId={}, addressId={}", userId, addressId);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ADDRESS_NOT_FOUND);
        }
        addressMapper.deleteById(addressId);
        log.info("收货地址删除成功, userId={}, addressId={}", userId, addressId);
    }

    /**
     * 取消用户的所有默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        List<UserAddress> defaultAddresses = addressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1));
        for (UserAddress addr : defaultAddresses) {
            addr.setIsDefault(0);
            addressMapper.updateById(addr);
        }
    }
}
