package com.shop.online;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用启动集成测试
 */
@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("应用集成测试")
class ShopOnlineApplicationTests {

    @Test
    @DisplayName("应用上下文加载成功")
    void contextLoads() {
    }
}
