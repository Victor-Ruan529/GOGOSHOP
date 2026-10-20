package com.example.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "成功"),

    PARAM_ERROR("400", "参数异常"),
    TOKEN_INVALID_ERROR("401", "无效的token"),
    TOKEN_CHECK_ERROR("401", "token验证失败，请重新登录"),
    NO_PERMISSION("403", "无权限操作"),
    PARAM_LOST_ERROR("4001", "参数缺失"),

    SYSTEM_ERROR("500", "系统异常"),
    USER_EXIST_ERROR("5001", "用户名已存在"),
    USER_NOT_LOGIN("5002", "用户未登录"),
    USER_ACCOUNT_ERROR("5003", "账号或密码错误"),
    USER_NOT_EXIST_ERROR("5004", "用户不存在"),
    PARAM_PASSWORD_ERROR("5005", "原密码输入错误"),
    COLLECT_ALREADY_ERROR("5006", "该商品已收藏"),
    LOGIN_LOCKED_ERROR("5007", "登录失败次数过多，请稍后再试"),
    GOODS_NOT_EXIST_ERROR("5008", "商品不存在"),
    STOCK_NOT_ENOUGH_ERROR("5009", "商品库存不足"),
    ORDER_NOT_EXIST_ERROR("5010", "订单不存在"),
    ORDER_STATUS_ERROR("5011", "当前订单状态不允许该操作"),
    BUSINESS_NOT_APPROVED_ERROR("5012", "只有审核通过的商家才能发布商品"),
    FILE_TYPE_ERROR("5013", "仅支持上传 jpg/png/gif/webp/bmp 格式的图片"),
    FILE_SIZE_ERROR("5014", "上传文件过大"),
    ;

    public final String code;
    public final String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
