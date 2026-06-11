package com.shop.online.module.order.mapper;

import com.shop.online.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品项 Mapper
 */
@Mapper
public interface OrderItemMapper {

    int insert(OrderItem item);

    int insertBatch(@Param("list") List<OrderItem> list);

    int updateById(OrderItem item);

    int deleteById(@Param("id") Long id);

    OrderItem selectById(@Param("id") Long id);

    List<OrderItem> selectList(@Param("query") OrderItem query);

    Long selectCount(@Param("query") OrderItem query);

    OrderItem selectOne(@Param("query") OrderItem query);
}
