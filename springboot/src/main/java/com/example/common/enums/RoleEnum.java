package com.example.common.enums;

public enum RoleEnum {
    /** 管理员 */
    ADMIN,
    /** 商家 */
    BUSINESS,
    /** 用户 */
    USER,
    ;

    public static RoleEnum of(String name) {
        for (RoleEnum role : values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }
        return null;
    }
}
