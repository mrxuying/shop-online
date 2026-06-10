package com.shop.online.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.online.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品项 Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
