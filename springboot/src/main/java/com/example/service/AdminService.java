package com.example.service;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AccountMapper;
import com.example.mapper.AdminMapper;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 管理员业务处理
 */
@Service
public class AdminService extends AbstractAccountService<Admin> {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public RoleEnum role() {
        return RoleEnum.ADMIN;
    }

    @Override
    protected AccountMapper<Admin> mapper() {
        return adminMapper;
    }

    @Override
    protected Admin newInstance() {
        return new Admin();
    }

    /** 管理员不能删除自己，避免系统里没有管理员 */
    @Override
    public void deleteById(Integer id) {
        if (id.equals(TokenUtils.requireCurrentUser().getId())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "不能删除当前登录的管理员");
        }
        super.deleteById(id);
    }
}
