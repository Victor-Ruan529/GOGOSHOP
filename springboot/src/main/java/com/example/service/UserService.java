package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.User;
import com.example.mapper.AccountMapper;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 普通用户业务处理
 */
@Service
public class UserService extends AbstractAccountService<User> {

    @Resource
    private UserMapper userMapper;

    @Override
    public RoleEnum role() {
        return RoleEnum.USER;
    }

    @Override
    protected AccountMapper<User> mapper() {
        return userMapper;
    }

    @Override
    protected User newInstance() {
        return new User();
    }
}
