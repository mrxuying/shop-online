package com.shop.online.module.payment.controller;

import com.shop.online.common.result.Result;
import com.shop.online.infrastructure.security.UserContext;
import com.shop.online.module.payment.dto.PayRequestDTO;
import com.shop.online.module.payment.service.IPayService;
import com.shop.online.module.payment.vo.PayResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
@Tag(name = "支付管理")
public class PayController {

    private final IPayService payService;

    @PostMapping("/{orderNo}")
    @Operation(summary = "发起支付")
    public Result<PayResultVO> pay(@PathVariable String orderNo,
                                    @Valid @RequestBody PayRequestDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(payService.pay(userId, orderNo, dto));
    }

    @GetMapping("/{orderNo}/result")
    @Operation(summary = "查询支付结果")
    public Result<PayResultVO> getPayResult(@PathVariable String orderNo) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(payService.getPayResult(userId, orderNo));
    }
}
