package com.example.service;

import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品分类业务处理
 */
@Service
public class TypeService {

    @Resource
    private TypeMapper typeMapper;

    public void add(Type type) {
        type.setId(null);
        typeMapper.insert(type);
    }

    public void deleteById(Integer id) {
        typeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(typeMapper::deleteById);
    }

    public void updateById(Type type) {
        typeMapper.updateById(type);
    }

    public Type selectById(Integer id) {
        return typeMapper.selectById(id);
    }

    public List<Type> selectAll(Type type) {
        return typeMapper.selectAll(type);
    }

    public PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(typeMapper.selectAll(type));
    }
}
