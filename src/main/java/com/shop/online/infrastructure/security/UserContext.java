package com.shop.online.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 当前用户上下文 — 存储在 SecurityContext 中
 */
@Getter
@AllArgsConstructor
public class UserContext {

    private final Long userId;
    private final String username;

    /**
     * 从 SecurityContextHolder 中获取当前登录用户
     */
    public static UserContext getCurrentUser() {
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserContext) {
            return (UserContext) principal;
        }
        return null;
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentUserId() {
        UserContext user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }
}
