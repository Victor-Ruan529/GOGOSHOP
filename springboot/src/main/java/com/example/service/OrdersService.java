package com.example.service;

import cn.hutool.core.util.IdUtil;
import com.example.common.enums.OrderStatusEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.exception.CustomException;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 订单业务处理
 */
@Service
public class OrdersService {

    static final int MAX_QUANTITY = 99;

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 下单：价格、商家、用户全部由服务端确定，前端只需要传 goodsId 和 quantity。
     * 扣库存和写订单在同一个事务里，库存不足时整体回滚。
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders create(Integer goodsId, Integer quantity) {
        Account current = TokenUtils.requireCurrentUser();
        int qty = quantity == null ? 1 : quantity;
        if (goodsId == null || qty < 1 || qty > MAX_QUANTITY) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "购买数量需在 1-" + MAX_QUANTITY + " 之间");
        }
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new CustomException(ResultCodeEnum.GOODS_NOT_EXIST_ERROR);
        }
        if (goodsMapper.decreaseStock(goodsId, qty) == 0) {
            throw new CustomException(ResultCodeEnum.STOCK_NOT_ENOUGH_ERROR);
        }

        Orders orders = new Orders();
        orders.setOrderId(IdUtil.getSnowflakeNextId());
        orders.setUserId(current.getId());
        orders.setBusinessId(goods.getBusinessId());
        orders.setGoodsId(goodsId);
        orders.setGoodsName(goods.getName());
        orders.setGoodsImg(goods.getImg());
        orders.setGoodsUnit(goods.getUnit());
        orders.setGoodsPrice(goods.getPrice());
        orders.setQuantity(qty);
        orders.setTotalPrice(goods.getPrice().multiply(BigDecimal.valueOf(qty)));
        orders.setStatus(OrderStatusEnum.TO_SHIP.status);
        orders.setCreateTime(LocalDateTime.now());
        ordersMapper.insert(orders);
        return orders;
    }

    /**
     * 修改订单状态，按状态机校验：商家发货、用户取消 / 确认收货。
     * 取消订单时回退库存。
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer id, String targetStatus) {
        Orders orders = getAccessible(id);
        RoleEnum role = TokenUtils.currentRole();
        OrderStatusEnum from = OrderStatusEnum.of(orders.getStatus());
        OrderStatusEnum to = OrderStatusEnum.of(targetStatus);
        if (from == null || !from.canTransitTo(to, role)) {
            throw new CustomException(ResultCodeEnum.ORDER_STATUS_ERROR);
        }
        // 带旧状态条件更新，并发时只有一个请求能成功
        if (ordersMapper.updateStatus(id, from.status, to.status) == 0) {
            throw new CustomException(ResultCodeEnum.ORDER_STATUS_ERROR);
        }
        if (to == OrderStatusEnum.CANCELLED && orders.getGoodsId() != null) {
            goodsMapper.increaseStock(orders.getGoodsId(), orders.getQuantity());
        }
    }

    /**
     * 删除：只能删除已结束（已完成 / 已取消）的订单；管理员不受限制
     */
    public void deleteById(Integer id) {
        Orders orders = getAccessible(id);
        OrderStatusEnum status = OrderStatusEnum.of(orders.getStatus());
        if (RoleEnum.ADMIN != TokenUtils.currentRole() && (status == null || !status.isFinished())) {
            throw new CustomException(ResultCodeEnum.ORDER_STATUS_ERROR.code, "只能删除已完成或已取消的订单");
        }
        ordersMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(this::deleteById);
    }

    public Orders selectById(Integer id) {
        return getAccessible(id);
    }

    public List<Orders> selectAll(Orders orders) {
        restrictToOwn(orders);
        return ordersMapper.selectAll(orders);
    }

    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        restrictToOwn(orders);
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(ordersMapper.selectAll(orders));
    }

    /** 用户只能看到自己的订单，商家只能看到自己店铺的订单 */
    private void restrictToOwn(Orders orders) {
        Account current = TokenUtils.requireCurrentUser();
        RoleEnum role = RoleEnum.of(current.getRole());
        if (role == RoleEnum.USER) {
            orders.setUserId(current.getId());
        } else if (role == RoleEnum.BUSINESS) {
            orders.setBusinessId(current.getId());
        }
    }

    /** 查询订单并校验当前用户是否有权访问，防止通过遍历 id 操作别人的订单 */
    private Orders getAccessible(Integer id) {
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            throw new CustomException(ResultCodeEnum.ORDER_NOT_EXIST_ERROR);
        }
        Account current = TokenUtils.requireCurrentUser();
        RoleEnum role = RoleEnum.of(current.getRole());
        boolean allowed = role == RoleEnum.ADMIN
                || role == RoleEnum.USER && Objects.equals(orders.getUserId(), current.getId())
                || role == RoleEnum.BUSINESS && Objects.equals(orders.getBusinessId(), current.getId());
        if (!allowed) {
            throw new CustomException(ResultCodeEnum.NO_PERMISSION);
        }
        return orders;
    }
}
