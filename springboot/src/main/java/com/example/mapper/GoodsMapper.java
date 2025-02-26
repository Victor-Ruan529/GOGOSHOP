package com.example.mapper;

import com.example.entity.Goods;
import com.example.entity.Orders;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作Goods相关数据接口
*/
public interface GoodsMapper {

    /**
      * 新增
    */
    int insert(Goods goods);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Goods goods);

    /**
      * 根据ID查询
    */
    Goods selectById(Integer id);

    /**
      * 查询所有
    */
    List<Goods> selectAll(Goods goods);

    @Select("select * from goods order by count desc limit 10")
    List<Goods> selectTop10();

    @Select("select * from goods where type_id = #{id}")
    List<Goods> selectByTypeId(Integer id);

    @Select("select * from goods where name like concat('%',#{name},'%')")
    List<Goods> selectByName(String name);

    @Select("SELECT * FROM goods ORDER BY RAND() LIMIT 2")
    List<Goods> selectRandomGoodsId(Integer id);

}