package com.example.mapper;

import com.example.entity.Carousel;

import java.util.List;

public interface CarouselMapper {

    int insert(Carousel entity);

    int deleteById(Integer id);

    int updateById(Carousel entity);

    Carousel selectById(Integer id);

    List<Carousel> selectAll(Carousel entity);
}
