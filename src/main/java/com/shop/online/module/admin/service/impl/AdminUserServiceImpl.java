package com.shop.online.module.admin.service.impl;

import com.shop.online.common.enums.UserStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.infrastructure.security.JwtUtils;
import com.shop.online.module.admin.dto.AdminLoginDTO;
import com.shop.online.module.admin.entity.AdminUser;
import com.shop.online.module.admin.mapper.AdminUserMapper;
import com.shop.online.module.admin.service.IAdminUserService;
import com.shop.online.module.admin.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements IAdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        AdminUser query = new AdminUser();
        query.setUsername(dto.getUsername());
        AdminUser admin = adminUserMapper.selectOne(query);

        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        if (Objects.equals(admin.getStatus(), UserStatusEnum.DISABLED.getCode())) {
            throw new BusinessException(ResultCode.ADMIN_DISABLED);
        }

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.ADMIN_PASSWORD_ERROR);
        }

        // 更新最后登录时间
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminUserMapper.updateById(admin);

        // 生成管理员 Token
        String accessToken = jwtUtils.generateAdminAccessToken(admin.getId(), admin.getUsername());

        log.info("管理员登录成功, adminId={}, username={}", admin.getId(), admin.getUsername());

        return AdminLoginVO.builder()
                .adminId(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .role(admin.getRole())
                .accessToken(accessToken)
                .build();
    }
}
