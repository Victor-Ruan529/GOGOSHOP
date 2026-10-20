package com.example.service;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 按角色找到对应的账号 Service，替代 Controller 里一长串 if (role == ...) 判断
 */
@Service
public class AccountRegistry {

    private final Map<RoleEnum, AbstractAccountService<?>> services = new EnumMap<>(RoleEnum.class);
    private final TokenUtils tokenUtils;

    public AccountRegistry(List<AbstractAccountService<?>> serviceList, TokenUtils tokenUtils) {
        serviceList.forEach(s -> services.put(s.role(), s));
        this.tokenUtils = tokenUtils;
    }

    public AbstractAccountService<?> get(String role) {
        AbstractAccountService<?> service = services.get(RoleEnum.of(role));
        if (service == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "角色不正确");
        }
        return service;
    }

    /**
     * 解析并校验 token，返回对应的账号
     */
    public Account authenticate(String token) {
        TokenUtils.TokenSubject subject = tokenUtils.decode(token);
        Account account = services.get(subject.role).selectById(subject.id);
        if (account == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR.code, "账号不存在，请重新登录");
        }
        tokenUtils.verify(token, account.getPassword());
        account.setRole(subject.role.name());
        return account;
    }
}
