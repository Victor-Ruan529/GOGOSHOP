package com.example.mapper;

import com.example.entity.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectMapper {

    int insert(Collect collect);

    int deleteById(Integer id);

    Collect selectById(Integer id);

    List<Collect> selectAll(Collect collect);

    Collect selectByGoodsIdAndUserId(@Param("goodsId") Integer goodsId, @Param("userId") Integer userId);
}
