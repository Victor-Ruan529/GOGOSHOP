package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.RoleEnum;
import com.example.entity.Business;
import com.example.service.BusinessService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商家接口
 */
@RestController
@RequestMapping("/business")
public class BusinessController {

    @Resource
    private BusinessService businessService;

    @RequireRole(RoleEnum.ADMIN)
    @PostMapping("/add")
    public Result add(@RequestBody Business business) {
        businessService.add(business);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        businessService.deleteById(id);
        return Result.success();
    }

    @RequireRole(RoleEnum.ADMIN)
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        businessService.deleteBatch(ids);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @PutMapping("/update")
    public Result updateById(@RequestBody Business business) {
        businessService.updateById(business);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TokenUtils.checkSelfOrAdmin(id, RoleEnum.BUSINESS);
        return Result.success(businessService.selectById(id));
    }

    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/selectAll")
    public Result selectAll(Business business) {
        return Result.success(businessService.selectAll(business));
    }

    @RequireRole(RoleEnum.ADMIN)
    @GetMapping("/selectPage")
    public Result selectPage(Business business,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Business> page = businessService.selectPage(business, pageNum, Math.min(pageSize, 100));
        return Result.success(page);
    }
}
