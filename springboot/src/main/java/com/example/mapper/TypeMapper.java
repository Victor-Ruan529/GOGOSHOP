package com.example.mapper;

import com.example.entity.Type;

import java.util.List;

public interface TypeMapper {

    int insert(Type entity);

    int deleteById(Integer id);

    int updateById(Type entity);

    Type selectById(Integer id);

    List<Type> selectAll(Type entity);
}
