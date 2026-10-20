package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单。goods* 字段是下单时的商品快照，不随商品修改而变化
 */
@Data
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 订单号（雪花算法，19 位，转成字符串返回避免 JS 精度丢失） */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    private Integer goodsId;
    private Integer businessId;
    private Integer userId;
    /** 待发货 / 已发货 / 已完成 / 已取消 */
    private String status;

    private String goodsName;
    private String goodsImg;
    private String goodsUnit;
    /** 成交单价 */
    private BigDecimal goodsPrice;
    /** 购买数量 */
    private Integer quantity;
    /** 订单总价 */
    private BigDecimal totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String businessName;
    private String userName;
    private String userPhone;
}
