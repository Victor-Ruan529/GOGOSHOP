package com.example.controller;

import com.example.common.Result;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.RoleEnum;
import com.example.entity.Goods;
import com.example.service.GoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品接口
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        goodsService.add(goods);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        goodsService.deleteById(id);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        goodsService.deleteBatch(ids);
        return Result.success();
    }

    @RequireRole({RoleEnum.ADMIN, RoleEnum.BUSINESS})
    @PutMapping("/update")
    public Result updateById(@RequestBody Goods goods) {
        goodsService.updateById(goods);
        return Result.success();
    }

    @GetMapping("/selectById")
    public Result selectById(@RequestParam Integer id) {
        return Result.success(goodsService.selectById(id));
    }

    @GetMapping("/selectAll")
    public Result selectAll(Goods goods) {
        return Result.success(goodsService.selectAll(goods));
    }

    @GetMapping("/selectPage")
    public Result selectPage(Goods goods,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Goods> page = goodsService.selectPage(goods, pageNum, Math.min(pageSize, 100));
        return Result.success(page);
    }

    @GetMapping("/selectTop10")
    public Result selectTop10() {
        return Result.success(goodsService.selectTop10());
    }

    @GetMapping("/selectByTypeId")
    public Result selectByTypeId(@RequestParam Integer id) {
        return Result.success(goodsService.selectByTypeId(id));
    }

    @GetMapping("/selectByName")
    public Result selectByName(@RequestParam String name) {
        return Result.success(goodsService.selectByName(name));
    }

    /** 随机推荐商品（保留旧接口名，id 参数已不再使用） */
    @GetMapping("/selectRandomGoodsId")
    public Result selectRandomGoodsId(@RequestParam(required = false) Integer id) {
        return Result.success(goodsService.selectRandom(2));
    }
}
