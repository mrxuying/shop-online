package com.shop.online.module.payment.mapper;

import com.shop.online.module.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 支付记录 Mapper
 */
@Mapper
public interface PaymentRecordMapper {

    int insert(PaymentRecord record);

    int updateById(PaymentRecord record);

    int deleteById(@Param("id") Long id);

    PaymentRecord selectById(@Param("id") Long id);

    List<PaymentRecord> selectList(@Param("query") PaymentRecord query);

    Long selectCount(@Param("query") PaymentRecord query);

    PaymentRecord selectOne(@Param("query") PaymentRecord query);
}
