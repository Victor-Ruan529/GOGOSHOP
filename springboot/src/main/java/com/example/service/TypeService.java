package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.example.mapper.TypeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品分类业务处理
 **/
@Service
public class TypeService {

    @Resource
    private TypeMapper typeMapper;

    /**
     * 新增
     */
    public void add(Type type) {
        type.setName(type.getName());
        typeMapper.insert(type);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        typeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            typeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Type Type) {
        typeMapper.updateById(Type);
    }

    /**
     * 根据ID查询
     */
    public Type selectById(Integer id) {
        return typeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Type> selectAll(Type Type) {
        return typeMapper.selectAll(Type);
    }

    /**
     * 分页查询
     */
    public PageInfo<Type> selectPage(Type Type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Type> list = typeMapper.selectAll(Type);
        return PageInfo.of(list);
    }

}