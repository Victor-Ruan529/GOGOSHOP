package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 普通用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
}
