package com.example.common;

public interface Constants {

    /** 请求头中携带 token 的字段名 */
    String TOKEN = "token";

    /** 拦截器解析出的当前登录账号，保存在 request attribute 中 */
    String CURRENT_ACCOUNT = "currentAccount";

    /** 管理员新增账号时未填写密码使用的默认密码 */
    String USER_DEFAULT_PASSWORD = "123456";
}
