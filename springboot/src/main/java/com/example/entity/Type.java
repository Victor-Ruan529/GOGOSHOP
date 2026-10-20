package com.example.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类
 */
@Data
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String description;
    private String img;
}
