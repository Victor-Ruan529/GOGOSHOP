package com.example.service;

import com.example.common.config.GogoshopProperties;
import com.example.common.enums.ResultCodeEnum;
import com.example.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败次数限制，防止暴力破解密码。
 * 单机内存实现，部署多实例时可替换为 Redis。
 */
@Service
public class LoginAttemptService {

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final GogoshopProperties properties;

    public LoginAttemptService(GogoshopProperties properties) {
        this.properties = properties;
    }

    public void checkLocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt != null && attempt.lockedUntil > System.currentTimeMillis()) {
            throw new CustomException(ResultCodeEnum.LOGIN_LOCKED_ERROR);
        }
    }

    public void onFailure(String key) {
        attempts.compute(key, (k, attempt) -> {
            if (attempt == null || attempt.lockedUntil != 0 && attempt.lockedUntil <= System.currentTimeMillis()) {
                attempt = new Attempt();
            }
            attempt.failures++;
            if (attempt.failures >= properties.getLogin().getMaxFailures()) {
                attempt.lockedUntil = System.currentTimeMillis() + properties.getLogin().getLockMinutes() * 60_000L;
            }
            return attempt;
        });
    }

    public void onSuccess(String key) {
        attempts.remove(key);
    }

    private static class Attempt {
        int failures;
        long lockedUntil;
    }
}
