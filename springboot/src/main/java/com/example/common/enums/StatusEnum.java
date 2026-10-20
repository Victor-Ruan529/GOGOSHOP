package com.example.common.enums;

/**
 * 商家审核状态
 */
public enum StatusEnum {
    CHECKING("审核中"),
    CHECK_OK("审核通过"),
    CHECK_NO("审核不通过"),
    ;

    public final String status;

    StatusEnum(String status) {
        this.status = status;
    }
}
