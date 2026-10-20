package com.example.service;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Collect;
import com.example.entity.Goods;
import com.example.exception.CustomException;
import com.example.mapper.CollectMapper;
import com.example.mapper.GoodsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 收藏业务处理
 */
@Service
public class CollectService {

    @Resource
    private CollectMapper collectMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /** 收藏：用户 id 取自 token，商家 id 取自商品，不信任前端传值 */
    public void add(Collect collect) {
        Account current = TokenUtils.requireCurrentUser();
        Goods goods = collect.getGoodsId() == null ? null : goodsMapper.selectById(collect.getGoodsId());
        if (goods == null) {
            throw new CustomException(ResultCodeEnum.GOODS_NOT_EXIST_ERROR);
        }
        if (collectMapper.selectByGoodsIdAndUserId(goods.getId(), current.getId()) != null) {
            throw new CustomException(ResultCodeEnum.COLLECT_ALREADY_ERROR);
        }
        Collect entity = new Collect();
        entity.setUserId(current.getId());
        entity.setGoodsId(goods.getId());
        entity.setBusinessId(goods.getBusinessId());
        collectMapper.insert(entity);
    }

    public void deleteById(Integer id) {
        Collect collect = collectMapper.selectById(id);
        if (collect == null) {
            return;
        }
        Account current = TokenUtils.requireCurrentUser();
        if (!RoleEnum.ADMIN.name().equals(current.getRole()) && !Objects.equals(collect.getUserId(), current.getId())) {
            throw new CustomException(ResultCodeEnum.NO_PERMISSION);
        }
        collectMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(this::deleteById);
    }

    public List<Collect> selectAll(Collect collect) {
        restrictToOwn(collect);
        return collectMapper.selectAll(collect);
    }

    public PageInfo<Collect> selectPage(Collect collect, Integer pageNum, Integer pageSize) {
        restrictToOwn(collect);
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(collectMapper.selectAll(collect));
    }

    private void restrictToOwn(Collect collect) {
        Account current = TokenUtils.requireCurrentUser();
        if (RoleEnum.USER.name().equals(current.getRole())) {
            collect.setUserId(current.getId());
        } else if (RoleEnum.BUSINESS.name().equals(current.getRole())) {
            collect.setBusinessId(current.getId());
        }
    }
}
