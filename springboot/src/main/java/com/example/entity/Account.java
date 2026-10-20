package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 三种角色（管理员、商家、用户）的公共父类
 */
@Data
public class Account {

    private Integer id;
    /** 用户名 */
    private String username;
    /** 名称 */
    private String name;
    /** 密码（BCrypt 哈希），只允许写入，永远不会序列化返回给前端 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    /** 角色标识 */
    private String role;
    /** 修改密码时的新密码 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;
    /** 头像 */
    private String avatar;
    /** 电话 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 登录成功后返回的 token，不入库 */
    private String token;
}
