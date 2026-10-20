package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.RoleEnum;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 普通用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.USER})
    @PutMapping("/update")
    public Result updateById(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.USER})
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TokenUtils.checkSelfOrAdmin(id, RoleEnum.USER);
        return Result.success(userService.selectById(id));
    }

    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/selectAll")
    public Result selectAll(User user) {
        return Result.success(userService.selectAll(user));
    }

    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/selectPage")
    public Result selectPage(User user,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> page = userService.selectPage(user, pageNum, Math.min(pageSize, 100));
        return Result.success(page);
    }
}
