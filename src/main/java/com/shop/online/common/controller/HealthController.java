package com.shop.online.common.controller;

import com.shop.online.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查 / 测试接口
 */
@RestController
@Tag(name = "健康检查")
public class HealthController {

    @GetMapping("/api/hello")
    @Operation(summary = "测试接口")
    public Result<Map<String, Object>> hello() {
        return Result.success(Map.of(
                "message", "Hello, Shop-Online!",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
