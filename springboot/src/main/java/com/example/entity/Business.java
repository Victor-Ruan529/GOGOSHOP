package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商家
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Business extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 商家介绍 */
    private String description;
    /** 审核状态：审核中 / 审核通过 / 审核不通过 */
    private String status;
}
