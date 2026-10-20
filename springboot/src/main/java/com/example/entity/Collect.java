package com.example.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收藏
 */
@Data
public class Collect implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private Integer businessId;
    private Integer goodsId;

    private String businessName;
    private String goodsName;
    private String goodsImg;
    private String goodsUnit;
    private BigDecimal goodsPrice;
}
