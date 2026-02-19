package com.watch.watch_mall.aop;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.watch.watch_mall.annotation.AuthCheck;
import com.watch.watch_mall.common.ErrorCode;
import com.watch.watch_mall.exception.BusinessException;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.enums.UserRoleEnum;
import com.watch.watch_mall.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class AuthInterceptor {

    @Resource
    UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String role = authCheck.role();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        if (StringUtils.isNotBlank(role)) {
            UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(role);
            if (roleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            if (!role.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        return joinPoint.proceed();
    }
}
