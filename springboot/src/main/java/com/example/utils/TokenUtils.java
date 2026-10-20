package com.example.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.common.Constants;
import com.example.common.config.GogoshopProperties;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Date;

/**
 * Token 工具类
 * <p>
 * 签名密钥 = 服务端密钥 + 用户密码哈希：
 * 1. 只知道用户密码无法伪造 token（旧实现直接用明文密码当密钥）；
 * 2. 用户修改密码后，之前签发的 token 自动失效。
 */
@Component
public class TokenUtils {

    private final GogoshopProperties properties;

    public TokenUtils(GogoshopProperties properties) {
        this.properties = properties;
    }

    /** 生成 token，载荷为 "id-ROLE" */
    public String createToken(Account account, RoleEnum role) {
        return JWT.create()
                .withAudience(account.getId() + "-" + role.name())
                .withIssuedAt(new Date())
                .withExpiresAt(DateUtil.offsetHour(new Date(), properties.getJwt().getExpireHours()))
                .sign(algorithm(account.getPassword()));
    }

    /** 不校验签名，仅解析出 token 中的用户 id 和角色，用于查库拿到密码哈希 */
    public TokenSubject decode(String token) {
        try {
            String[] parts = JWT.decode(token).getAudience().get(0).split("-");
            RoleEnum role = RoleEnum.of(parts[1]);
            if (role == null) {
                throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
            }
            return new TokenSubject(Integer.valueOf(parts[0]), role);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
    }

    /** 校验签名和过期时间 */
    public void verify(String token, String passwordHash) {
        try {
            DecodedJWT ignored = JWT.require(algorithm(passwordHash)).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
    }

    private Algorithm algorithm(String passwordHash) {
        return Algorithm.HMAC256(properties.getJwt().getSecret() + ":" + passwordHash);
    }

    /**
     * 获取当前登录的账号（由 JwtInterceptor 放入 request），未登录时返回空 Account
     */
    public static Account getCurrentUser() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return new Account();
        }
        Object account = attributes.getAttribute(Constants.CURRENT_ACCOUNT, RequestAttributes.SCOPE_REQUEST);
        return account instanceof Account ? (Account) account : new Account();
    }

    /** 获取当前登录账号，未登录直接抛异常 */
    public static Account requireCurrentUser() {
        Account account = getCurrentUser();
        if (account.getId() == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_LOGIN);
        }
        return account;
    }

    /** 只允许管理员或账号本人访问（例如查看个人资料） */
    public static void checkSelfOrAdmin(Integer accountId, RoleEnum accountRole) {
        Account current = requireCurrentUser();
        boolean self = accountRole.name().equals(current.getRole()) && current.getId().equals(accountId);
        if (!self && !RoleEnum.ADMIN.name().equals(current.getRole())) {
            throw new CustomException(ResultCodeEnum.NO_PERMISSION);
        }
    }

    public static RoleEnum currentRole() {
        return RoleEnum.of(getCurrentUser().getRole());
    }

    public static class TokenSubject {
        public final Integer id;
        public final RoleEnum role;

        public TokenSubject(Integer id, RoleEnum role) {
            this.id = id;
            this.role = role;
        }
    }
}
