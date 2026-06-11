package com.shop.online.module.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.PageResult;
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

import java.time.LocalDateTime;
import java.util.List;
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
    public PageResult<UserProfileVO> pageUsers(AdminUserQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        PageHelper.orderBy("create_time desc");

        List<User> list;
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            // 关键词搜索：用户名或手机号模糊匹配
            list = userMapper.selectByKeyword(dto.getKeyword());
        } else if (dto.getStatus() != null) {
            User query = new User();
            query.setStatus(dto.getStatus());
            list = userMapper.selectList(query);
        } else {
            list = userMapper.selectList(null);
        }

        PageInfo<User> pageInfo = new PageInfo<>(list);
        List<UserProfileVO> records = list.stream().map(userConverter::toProfileVO).toList();
        return PageResult.of(pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize(), records);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户状态变更成功, userId={}, status={}", userId, status);
    }
}
