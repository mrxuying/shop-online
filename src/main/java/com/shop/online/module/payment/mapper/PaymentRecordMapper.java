package com.shop.online.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.online.module.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录 Mapper
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
