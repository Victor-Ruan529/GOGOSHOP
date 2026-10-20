package com.example.mapper;

import com.example.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {

    int insert(Goods goods);

    int deleteById(Integer id);

    int updateById(Goods goods);

    Goods selectById(Integer id);

    List<Goods> selectAll(Goods goods);

    List<Goods> selectTop10();

    List<Goods> selectByTypeId(Integer typeId);

    List<Goods> selectByName(String name);

    List<Goods> selectRandom(@Param("limit") int limit);

    /**
     * 原子扣减库存并增加销量，库存不足时返回 0，避免并发超卖
     */
    int decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /** 取消订单时回退库存和销量 */
    int increaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
}
