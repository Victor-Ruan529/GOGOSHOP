package com.example.common.config;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Constants;
import com.example.common.annotation.PublicApi;
import com.example.common.annotation.RequireRole;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AccountRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt 拦截器：认证（token 是否有效）+ 授权（角色是否允许访问）
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private AccountRegistry accountRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        if (findAnnotation(method, PublicApi.class) != null) {
            return true;
        }

        // 只从请求头取 token，不再支持 URL 参数（会被记录到日志和浏览器历史里）
        String token = request.getHeader(Constants.TOKEN);
        if (StrUtil.isBlank(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }
        Account account = accountRegistry.authenticate(token);
        request.setAttribute(Constants.CURRENT_ACCOUNT, account);

        RequireRole requireRole = findAnnotation(method, RequireRole.class);
        if (requireRole != null && !ArrayUtil.contains(requireRole.value(), RoleEnum.of(account.getRole()))) {
            throw new CustomException(ResultCodeEnum.NO_PERMISSION);
        }
        return true;
    }

    /** 方法上的注解优先，其次是类上的 */
    private <A extends java.lang.annotation.Annotation> A findAnnotation(HandlerMethod method, Class<A> type) {
        A annotation = AnnotatedElementUtils.findMergedAnnotation(method.getMethod(), type);
        return annotation != null ? annotation : AnnotatedElementUtils.findMergedAnnotation(method.getBeanType(), type);
    }
}
