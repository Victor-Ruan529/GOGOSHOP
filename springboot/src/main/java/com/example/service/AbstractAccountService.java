package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.mapper.AccountMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员 / 商家 / 用户 三种账号的公共逻辑（登录、注册、改密码、增删改查）。
 * 原来三个 Service 各写了一遍几乎相同的代码，这里统一实现，子类只需提供 Mapper 和角色。
 */
public abstract class AbstractAccountService<T extends Account> {

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_]{3,20}$";
    private static final int PASSWORD_MIN_LENGTH = 6;
    private static final int PASSWORD_MAX_LENGTH = 32;

    @Resource
    protected PasswordEncoder passwordEncoder;
    @Resource
    protected TokenUtils tokenUtils;
    @Resource
    protected LoginAttemptService loginAttemptService;

    public abstract RoleEnum role();

    protected abstract AccountMapper<T> mapper();

    protected abstract T newInstance();

    /** 新增前的扩展点，例如商家默认为"审核中" */
    protected void beforeInsert(T account) {
    }

    /** 非管理员修改自己资料时，清除不允许自己修改的字段 */
    protected void clearProtectedFields(T account) {
    }

    /**
     * 新增账号（管理员后台新增 / 注册共用）
     */
    public void add(T account) {
        if (StrUtil.isBlank(account.getUsername()) || !ReUtil.isMatch(USERNAME_PATTERN, account.getUsername())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "用户名只能包含字母、数字、下划线，长度 3-20 位");
        }
        if (ObjectUtil.isNotNull(mapper().selectByUsername(account.getUsername()))) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        String rawPassword = StrUtil.isBlank(account.getPassword()) ? Constants.USER_DEFAULT_PASSWORD : account.getPassword();
        checkPasswordStrength(rawPassword);
        account.setPassword(passwordEncoder.encode(rawPassword));
        if (StrUtil.isBlank(account.getName())) {
            account.setName(account.getUsername());
        }
        account.setId(null);
        account.setRole(role().name());
        beforeInsert(account);
        mapper().insert(account);
    }

    /**
     * 注册：只拷贝允许用户自己填写的字段，防止通过请求体注入 role / status / id
     */
    public void register(Account form) {
        if (StrUtil.isBlank(form.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        T account = newInstance();
        account.setUsername(form.getUsername());
        account.setPassword(form.getPassword());
        account.setName(form.getName());
        account.setPhone(form.getPhone());
        account.setEmail(form.getEmail());
        add(account);
    }

    /**
     * 登录
     */
    public Account login(Account form) {
        String lockKey = role().name() + ":" + form.getUsername();
        loginAttemptService.checkLocked(lockKey);
        T dbAccount = mapper().selectByUsername(form.getUsername());
        // 用户不存在和密码错误返回同样的提示，避免被用来探测用户名
        if (dbAccount == null || !passwordMatches(form.getPassword(), dbAccount)) {
            loginAttemptService.onFailure(lockKey);
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        loginAttemptService.onSuccess(lockKey);
        dbAccount.setToken(tokenUtils.createToken(dbAccount, role()));
        return dbAccount;
    }

    /**
     * 修改当前登录用户自己的密码
     */
    public void updatePassword(Account current, String oldPassword, String newPassword) {
        T dbAccount = mapper().selectById(current.getId());
        if (dbAccount == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!passwordMatches(oldPassword, dbAccount)) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        checkPasswordStrength(newPassword);
        T update = newInstance();
        update.setId(dbAccount.getId());
        update.setPassword(passwordEncoder.encode(newPassword));
        mapper().updateById(update);
    }

    /**
     * 修改资料：管理员可改任意账号；其他角色只能改自己，且不能改密码、角色、审核状态
     */
    public void updateById(T account) {
        Account current = TokenUtils.requireCurrentUser();
        account.setRole(null);
        if (RoleEnum.ADMIN.name().equals(current.getRole())) {
            if (StrUtil.isNotBlank(account.getPassword())) {
                checkPasswordStrength(account.getPassword());
                account.setPassword(passwordEncoder.encode(account.getPassword()));
            } else {
                account.setPassword(null);
            }
        } else {
            account.setId(current.getId());
            account.setPassword(null);
            clearProtectedFields(account);
        }
        if (account.getId() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (StrUtil.isNotBlank(account.getUsername())) {
            T exist = mapper().selectByUsername(account.getUsername());
            if (exist != null && !exist.getId().equals(account.getId())) {
                throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
            }
        }
        mapper().updateById(account);
    }

    public void deleteById(Integer id) {
        mapper().deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        ids.forEach(this::deleteById);
    }

    public T selectById(Integer id) {
        return mapper().selectById(id);
    }

    public List<T> selectAll(T account) {
        return mapper().selectAll(account);
    }

    public PageInfo<T> selectPage(T account, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(mapper().selectAll(account));
    }

    /**
     * 校验密码。兼容旧版本数据库里的明文密码：校验通过后自动升级为 BCrypt 哈希
     */
    private boolean passwordMatches(String rawPassword, T dbAccount) {
        if (StrUtil.isBlank(rawPassword) || StrUtil.isBlank(dbAccount.getPassword())) {
            return false;
        }
        String stored = dbAccount.getPassword();
        if (stored.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        if (!stored.equals(rawPassword)) {
            return false;
        }
        T update = newInstance();
        update.setId(dbAccount.getId());
        update.setPassword(passwordEncoder.encode(rawPassword));
        mapper().updateById(update);
        dbAccount.setPassword(update.getPassword());
        return true;
    }

    private void checkPasswordStrength(String password) {
        if (password == null || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR.code, "密码长度需为 6-32 位");
        }
    }
}
