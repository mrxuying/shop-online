package com.shop.online.module.payment.service;

import com.shop.online.module.payment.dto.PayRequestDTO;
import com.shop.online.module.payment.vo.PayResultVO;

/**
 * 支付服务接口
 */
public interface IPayService {

    /**
     * 发起支付（模拟）
     */
    PayResultVO pay(Long userId, String orderNo, PayRequestDTO dto);

    /**
     * 查询支付结果
     */
    PayResultVO getPayResult(Long userId, String orderNo);

    /**
     * 处理退款
     */
    void refund(String orderNo);
}
