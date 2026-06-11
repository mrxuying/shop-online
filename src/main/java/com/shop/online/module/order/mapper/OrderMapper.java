package com.shop.online.module.order.mapper;

import com.shop.online.module.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper {

    int insert(Order order);

    int updateById(Order order);

    int deleteById(@Param("id") Long id);

    Order selectById(@Param("id") Long id);

    List<Order> selectList(@Param("query") Order query);

    Long selectCount(@Param("query") Order query);

    Order selectOne(@Param("query") Order query);

    /**
     * 高级查询（支持时间范围与状态IN过滤）
     */
    List<Order> selectListAdvanced(@Param("query") Order query,
                                   @Param("statusList") java.util.List<Integer> statusList,
                                   @Param("createTimeGe") java.time.LocalDateTime createTimeGe,
                                   @Param("createTimeLt") java.time.LocalDateTime createTimeLt);

    /**
     * 高级计数查询
     */
    Long selectCountAdvanced(@Param("query") Order query,
                             @Param("statusList") java.util.List<Integer> statusList,
                             @Param("createTimeGe") java.time.LocalDateTime createTimeGe,
                             @Param("createTimeLt") java.time.LocalDateTime createTimeLt);
}
