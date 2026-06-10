package com.shop.online.module.admin.service;

import com.shop.online.module.admin.dto.AdminLoginDTO;
import com.shop.online.module.admin.vo.AdminLoginVO;

/**
 * 管理员服务接口
 */
public interface IAdminUserService {

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO dto);
}
