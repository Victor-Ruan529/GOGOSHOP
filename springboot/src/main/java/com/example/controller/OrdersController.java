package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.RoleEnum;
import com.example.entity.Orders;
import com.example.service.OrdersService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 下单：只需要 goodsId 和 quantity，价格、用户、商家由服务端确定
     */
    @RequireRole(RoleEnum.USER)
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        return Result.success(ordersService.create(orders.getGoodsId(), orders.getQuantity()));
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        ordersService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        ordersService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改订单状态（发货 / 取消 / 确认收货），只接受 id 和 status，其他字段忽略
     */
    @PutMapping("/update")
    public Result updateStatus(@RequestBody Orders orders) {
        ordersService.changeStatus(orders.getId(), orders.getStatus());
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        return Result.success(ordersService.selectById(id));
    }

    @GetMapping("/selectAll")
    public Result selectAll(Orders orders) {
        return Result.success(ordersService.selectAll(orders));
    }

    @GetMapping("/selectPage")
    public Result selectPage(Orders orders,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Orders> page = ordersService.selectPage(orders, pageNum, Math.min(pageSize, 100));
        return Result.success(page);
    }
}
