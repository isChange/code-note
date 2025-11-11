package com.ly.app.common.aop;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ly.app.common.annotation.PreventRepeatSubmit;
import com.ly.app.common.cache.RedisCache;
import com.ly.app.common.constant.UserConstant;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.exception.BaseException;
import com.ly.app.common.units.ServletUtil;
import com.ly.app.domain.vo.user.SafeUserVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 防抖切面
 * 使用 Redis 实现接口防抖，防止用户短时间内重复提交表单
 *
 * @author liu yi
 */
@Slf4j
@Aspect
@Component
public class PreventRepeatSubmitInterceptor {

    /**
     * Redis key 前缀
     */
    private static final String REPEAT_SUBMIT_KEY = "prevent:repeat:submit:";

    @Around("@annotation(preventRepeatSubmit)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, PreventRepeatSubmit preventRepeatSubmit) throws Throwable {
        // 1. 获取注解参数
        int interval = preventRepeatSubmit.interval();
        String message = preventRepeatSubmit.message();
        boolean needLogin = preventRepeatSubmit.needLogin();

        // 2. 生成唯一标识
        String uniqueKey = generateUniqueKey(joinPoint, needLogin);

        // 3. 拼接 Redis key
        String redisKey = REPEAT_SUBMIT_KEY + uniqueKey;

        // 4. 检查是否存在（存在说明重复提交）
        Object existValue = RedisCache.getObject(redisKey);
        if (existValue != null) {
            log.warn("检测到重复提交，key: {}", redisKey);
            throw new BaseException(ErrorCode.OPERATION_ERROR, message);
        }

        // 5. 设置标识，并设置过期时间
        RedisCache.setObject(redisKey, System.currentTimeMillis(), interval, TimeUnit.SECONDS);

        // 6. 执行方法
        return joinPoint.proceed();
    }

    /**
     * 生成唯一标识
     *
     * @param joinPoint  切点
     * @param needLogin  是否需要登录
     * @return 唯一标识
     */
    private String generateUniqueKey(ProceedingJoinPoint joinPoint, boolean needLogin) {
        // 1. 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        // 2. 获取请求信息
        HttpServletRequest request = ServletUtil.getRequest();
        String uri = request.getRequestURI();

        // 3. 获取用户标识或 IP
        String userIdentifier;
        if (needLogin) {
            // 获取用户 ID
            SafeUserVO loginUser = (SafeUserVO) ServletUtil.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
            if (loginUser != null && loginUser.getId() != null) {
                userIdentifier = loginUser.getId().toString();
            } else {
                // 如果需要登录但未登录，使用 IP
                userIdentifier = getClientIp(request);
            }
        } else {
            // 使用 IP 地址
            userIdentifier = getClientIp(request);
        }

        // 4. 拼接并生成 MD5
        String rawKey = StrUtil.format("{}:{}:{}:{}", className, methodName, uri, userIdentifier);
        return DigestUtil.md5Hex(rawKey);
    }

    /**
     * 获取客户端 IP 地址
     *
     * @param request 请求对象
     * @return IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个 IP 的情况，取第一个
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return StrUtil.isBlank(ip) ? "unknown" : ip;
    }
}