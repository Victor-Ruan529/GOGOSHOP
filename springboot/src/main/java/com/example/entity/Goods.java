package com.example.entity;

import java.io.Serializable;

/**
 * 商品
*/
public class Goods extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Integer id;
    /** 商品名称 */
    private String name;
    /** 商品图 */
    private String img;
    /** 商品介绍 */
    private String description;
    /** 商品价格 */
    private Double price;
    /** 计件单位 */
    private String unit;
    /** 商品销量 */
    private Integer count;
    /** 商品分类ID */
    private Integer typeId;
    /** 商家ID */
    private Integer businessId;
    private String typeName;
    private String businessName;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

}