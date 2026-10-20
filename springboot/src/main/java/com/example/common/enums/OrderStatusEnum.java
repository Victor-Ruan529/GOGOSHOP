package com.example.common.enums;

import java.util.Arrays;

/**
 * 订单状态及合法流转：
 * 待发货 -> 已发货（商家/管理员）
 * 待发货 -> 已取消（用户，库存回退）
 * 已发货 -> 已完成（用户确认收货）
 */
public enum OrderStatusEnum {
    TO_SHIP("待发货"),
    SHIPPED("已发货"),
    DONE("已完成"),
    CANCELLED("已取消"),
    ;

    public final String status;

    OrderStatusEnum(String status) {
        this.status = status;
    }

    public static OrderStatusEnum of(String status) {
        return Arrays.stream(values()).filter(s -> s.status.equals(status)).findFirst().orElse(null);
    }

    /** 订单是否已结束（结束后才允许删除） */
    public boolean isFinished() {
        return this == DONE || this == CANCELLED;
    }

    /** 判断某个角色能否把订单从当前状态改为目标状态 */
    public boolean canTransitTo(OrderStatusEnum target, RoleEnum role) {
        if (target == null) {
            return false;
        }
        switch (role) {
            case BUSINESS:
            case ADMIN:
                return this == TO_SHIP && target == SHIPPED;
            case USER:
                return (this == TO_SHIP && target == CANCELLED) || (this == SHIPPED && target == DONE);
            default:
                return false;
        }
    }
}
