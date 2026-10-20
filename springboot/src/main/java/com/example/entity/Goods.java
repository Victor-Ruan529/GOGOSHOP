package com.example.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品
 */
@Data
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 商品名称 */
    private String name;
    /** 商品图 */
    private String img;
    /** 商品介绍（富文本） */
    private String description;
    /** 商品价格，金额统一用 BigDecimal 避免精度问题 */
    private BigDecimal price;
    /** 计件单位 */
    private String unit;
    /** 销量 */
    private Integer count;
    /** 库存 */
    private Integer stock;
    /** 分类ID */
    private Integer typeId;
    /** 商家ID */
    private Integer businessId;

    private String typeName;
    private String businessName;
}
