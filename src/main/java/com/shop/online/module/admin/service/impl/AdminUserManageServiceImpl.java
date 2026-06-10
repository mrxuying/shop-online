package com.shop.online.module.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.module.admin.dto.AdminUserQueryDTO;
import com.shop.online.module.admin.service.IAdminUserManageService;
import com.shop.online.module.user.converter.UserConverter;
import com.shop.online.module.user.entity.User;
import com.shop.online.module.user.mapper.UserMapper;
import com.shop.online.module.user.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 管理端用户管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserManageServiceImpl implements IAdminUserManageService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public IPage<UserProfileVO> pageUsers(AdminUserQueryDTO dto) {
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Objects.nonNull(dto.getStatus()), User::getStatus, dto.getStatus())
                .and(StrUtil.isNotBlank(dto.getKeyword()), w ->
                        w.like(User::getUsername, dto.getKeyword())
                                .or()
                                .like(User::getPhone, dto.getKeyword()))
                .orderByDesc(User::getCreateTime);

        return userMapper.selectPage(page, wrapper).convert(userConverter::toProfileVO);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
        log.info("用户状态变更成功, userId={}, status={}", userId, status);
    }
}
