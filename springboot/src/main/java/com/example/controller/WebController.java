package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.annotation.PublicApi;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AccountRegistry;
import com.example.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 登录、注册、修改密码
 */
@RestController
public class WebController {

    @Resource
    private AccountRegistry accountRegistry;

    @PublicApi
    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

    @PublicApi
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (StrUtil.hasBlank(account.getUsername(), account.getPassword(), account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        return Result.success(accountRegistry.get(account.getRole()).login(account));
    }

    /**
     * 注册：只允许注册商家和普通用户，管理员只能由已有管理员在后台创建
     */
    @PublicApi
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.hasBlank(account.getUsername(), account.getPassword(), account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        RoleEnum role = RoleEnum.of(account.getRole());
        if (role != RoleEnum.USER && role != RoleEnum.BUSINESS) {
            return Result.error(ResultCodeEnum.NO_PERMISSION.code, "只能注册商家或用户账号");
        }
        accountRegistry.get(account.getRole()).register(account);
        return Result.success();
    }

    /**
     * 修改密码：只能修改当前登录账号自己的密码（原来按请求体中的用户名修改）
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.hasBlank(account.getPassword(), account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Account current = TokenUtils.requireCurrentUser();
        accountRegistry.get(current.getRole()).updatePassword(current, account.getPassword(), account.getNewPassword());
        return Result.success();
    }
}
