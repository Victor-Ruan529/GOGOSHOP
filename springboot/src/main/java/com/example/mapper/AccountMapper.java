package com.example.mapper;

import com.example.entity.Account;

import java.util.List;

/**
 * 管理员、商家、用户三张账号表的通用操作
 */
public interface AccountMapper<T extends Account> {

    int insert(T account);

    int deleteById(Integer id);

    int updateById(T account);

    T selectById(Integer id);

    List<T> selectAll(T account);

    T selectByUsername(String username);
}
