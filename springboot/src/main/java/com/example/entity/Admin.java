package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends Account implements Serializable {
    private static final long serialVersionUID = 1L;
}
