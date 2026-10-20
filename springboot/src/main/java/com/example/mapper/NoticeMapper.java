package com.example.mapper;

import com.example.entity.Notice;

import java.util.List;

public interface NoticeMapper {

    int insert(Notice entity);

    int deleteById(Integer id);

    int updateById(Notice entity);

    Notice selectById(Integer id);

    List<Notice> selectAll(Notice entity);
}
