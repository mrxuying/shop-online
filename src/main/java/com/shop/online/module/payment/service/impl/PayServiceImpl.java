package com.shop.online.module.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shop.online.common.enums.OrderStatusEnum;
import com.shop.online.common.enums.PayStatusEnum;
import com.shop.online.common.exception.BusinessException;
import com.shop.online.common.result.ResultCode;
import com.shop.online.common.utils.OrderNoGenerator;
import com.shop.online.module.order.entity.Order;
import com.shop.online.module.order.mapper.OrderMapper;
import com.shop.online.module.payment.dto.PayRequestDTO;
import com.shop.online.module.payment.entity.PaymentRecord;
import com.shop.online.module.payment.mapper.PaymentRecordMapper;
import com.shop.online.module.payment.service.IPayService;
import com.shop.online.module.payment.vo.PayResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 支付服务实现（模拟支付）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements IPayService {

    private final OrderMapper orderMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final OrderNoGenerator orderNoGenerator;

    @Override
    @Transactional
    public PayResultVO pay(Long userId, String orderNo, PayRequestDTO dto) {
        // 查询订单
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PAY_ORDER_NOT_FOUND);
        }

        // 校验订单状态
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.PENDING_PAYMENT.getCode())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        // 检查是否已有支付记录
        PaymentRecord existingRecord = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>().eq(PaymentRecord::getOrderNo, orderNo));
        if (existingRecord != null && Objects.equals(existingRecord.getStatus(), PayStatusEnum.SUCCESS.getCode())) {
            throw new BusinessException(ResultCode.PAY_FAILED.getCode(), "该订单已支付");
        }

        // 生成支付流水号
        String payNo = orderNoGenerator.generatePayNo();

        // 模拟支付（90% 概率成功）
        boolean paySuccess = Math.random() < 0.9;

        PaymentRecord record = new PaymentRecord();
        record.setOrderNo(orderNo);
        record.setPayNo(payNo);
        record.setPayType(dto.getPayType());
        record.setPayAmount(order.getPayAmount());

        if (paySuccess) {
            record.setStatus(PayStatusEnum.SUCCESS.getCode());
            record.setThirdPartyNo("MOCK" + System.currentTimeMillis());
            record.setPayTime(LocalDateTime.now());

            // 更新订单状态
            order.setStatus(OrderStatusEnum.PENDING_DELIVERY.getCode());
            order.setPayType(dto.getPayType());
            order.setPaymentTime(LocalDateTime.now());
            orderMapper.updateById(order);

            log.info("支付成功, orderNo={}, payNo={}, amount={}", orderNo, payNo, order.getPayAmount());
        } else {
            record.setStatus(PayStatusEnum.FAILED.getCode());
            log.warn("支付失败, orderNo={}, payNo={}", orderNo, payNo);
        }

        paymentRecordMapper.insert(record);

        return PayResultVO.builder()
                .payNo(payNo)
                .orderNo(orderNo)
                .payAmount(order.getPayAmount())
                .status(record.getStatus())
                .payTime(record.getPayTime())
                .build();
    }

    @Override
    public PayResultVO getPayResult(Long userId, String orderNo) {
        // 校验订单归属
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        PaymentRecord record = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderNo, orderNo)
                        .orderByDesc(PaymentRecord::getCreateTime)
                        .last("LIMIT 1"));

        if (record == null) {
            throw new BusinessException(ResultCode.PAY_ORDER_NOT_FOUND);
        }

        return PayResultVO.builder()
                .payNo(record.getPayNo())
                .orderNo(orderNo)
                .payAmount(record.getPayAmount())
                .status(record.getStatus())
                .payTime(record.getPayTime())
                .build();
    }

    @Override
    @Transactional
    public void refund(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        // 查询支付记录
        PaymentRecord payRecord = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderNo, orderNo)
                        .eq(PaymentRecord::getStatus, PayStatusEnum.SUCCESS.getCode()));

        if (payRecord == null) {
            throw new BusinessException(ResultCode.REFUND_FAILED.getCode(), "未找到成功支付记录");
        }

        // 更新支付记录为已退款
        payRecord.setStatus(PayStatusEnum.REFUNDED.getCode());
        payRecord.setRefundTime(LocalDateTime.now());
        paymentRecordMapper.updateById(payRecord);

        // 更新订单状态
        order.setStatus(OrderStatusEnum.REFUNDING.getCode());
        orderMapper.updateById(order);

        log.info("退款处理完成, orderNo={}, refundAmount={}", orderNo, payRecord.getPayAmount());
    }
}
