package com.example.service;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Goods;
import com.example.exception.CustomException;
import com.example.mapper.GoodsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 商品信息业务处理
 */
@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private BusinessService businessService;

    /**
     * 新增：商家只能给自己的店铺发布商品，且必须审核通过
     */
    public void add(Goods goods) {
        Account current = TokenUtils.requireCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(current.getRole())) {
            if (!businessService.isApproved(current.getId())) {
                throw new CustomException(ResultCodeEnum.BUSINESS_NOT_APPROVED_ERROR);
            }
            goods.setBusinessId(current.getId());
        }
        validate(goods, true);
        goods.setId(null);
        goods.setCount(0);
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        goodsMapper.insert(goods);
    }

    public void deleteById(Integer id) {
        checkOwner(id);
        goodsMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(this::deleteById);
    }

    /**
     * 修改：商家只能改自己的商品，不能把商品转给别的商家，也不能改销量
     */
    public void updateById(Goods goods) {
        if (goods.getId() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        checkOwner(goods.getId());
        if (RoleEnum.BUSINESS == TokenUtils.currentRole()) {
            goods.setBusinessId(null);
            goods.setCount(null);
        }
        validate(goods, false);
        goodsMapper.updateById(goods);
    }

    public Goods selectById(Integer id) {
        return goodsMapper.selectById(id);
    }

    public List<Goods> selectAll(Goods goods) {
        restrictToOwnShop(goods);
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询：商家后台只看到自己的商品
     * （旧代码对 USER 角色也设置了 businessId = 用户id，属于 bug，已删除）
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        restrictToOwnShop(goods);
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(goodsMapper.selectAll(goods));
    }

    public List<Goods> selectTop10() {
        return goodsMapper.selectTop10();
    }

    public List<Goods> selectByTypeId(Integer typeId) {
        return goodsMapper.selectByTypeId(typeId);
    }

    public List<Goods> selectByName(String name) {
        return goodsMapper.selectByName(name);
    }

    public List<Goods> selectRandom(int limit) {
        return goodsMapper.selectRandom(limit);
    }

    private void restrictToOwnShop(Goods goods) {
        Account current = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(current.getRole())) {
            goods.setBusinessId(current.getId());
        }
    }

    /** 商家只能操作自己店铺的商品，管理员不受限制 */
    private void checkOwner(Integer goodsId) {
        Goods db = goodsMapper.selectById(goodsId);
        if (db == null) {
            throw new CustomException(ResultCodeEnum.GOODS_NOT_EXIST_ERROR);
        }
        Account current = TokenUtils.requireCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(current.getRole()) && !Objects.equals(db.getBusinessId(), current.getId())) {
            throw new CustomException(ResultCodeEnum.NO_PERMISSION);
        }
    }

    private void validate(Goods goods, boolean creating) {
        if (creating && (goods.getName() == null || goods.getName().trim().isEmpty())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "商品名称不能为空");
        }
        if (creating && goods.getPrice() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "商品价格不能为空");
        }
        if (goods.getPrice() != null && goods.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "商品价格不能为负数");
        }
        if (goods.getStock() != null && goods.getStock() < 0) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "库存不能为负数");
        }
    }
}
