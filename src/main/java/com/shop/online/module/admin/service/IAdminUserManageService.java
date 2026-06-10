package com.shop.online.module.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shop.online.module.admin.dto.AdminUserQueryDTO;
import com.shop.online.module.user.vo.UserProfileVO;

/**
 * 管理端用户管理服务接口
 */
public interface IAdminUserManageService {

    /**
     * 用户列表查询
     */
    IPage<UserProfileVO> pageUsers(AdminUserQueryDTO dto);

    /**
     * 用户状态变更（禁用/启用）
     */
    void updateUserStatus(Long userId, Integer status);
}
