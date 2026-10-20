package com.example.mapper;

import com.example.entity.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdersMapper {

    int insert(Orders orders);

    int deleteById(Integer id);

    Orders selectById(Integer id);

    List<Orders> selectAll(Orders orders);

    /**
     * 带旧状态条件的更新（乐观锁思想），防止并发下状态被重复修改
     */
    int updateStatus(@Param("id") Integer id, @Param("fromStatus") String fromStatus, @Param("toStatus") String toStatus);
}
