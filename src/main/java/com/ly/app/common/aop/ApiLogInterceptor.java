package com.ly.app.common.aop;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.ly.app.common.annotation.ApiLog;
import com.ly.app.common.units.ServletUtil;
import com.ly.app.domain.dto.log.ApiLogDTO;
import com.ly.app.domain.vo.user.SafeUserVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * API日志记录拦截器
 *
 * @author liu yi
 * @version v1.0.0
 * @Description 通过AOP技术拦截@ApiLog注解的方法，记录请求和响应日志
 * @createDate 2025/11/9
 */
@Aspect
@Component
@Slf4j
public class ApiLogInterceptor {

    /**
     * 环绕通知：记录请求和响应
     */
    @Around("@annotation(apiLog)")
    public Object around(ProceedingJoinPoint joinPoint, ApiLog apiLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        try {

            // 构建日志信息
            ApiLogDTO logDTO = buildApiLogDTO(joinPoint, apiLog, startTime);

            // 执行目标方法
            result = joinPoint.proceed();

            // 处理响应
            handleResponse(logDTO, result);

            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();

            // 构建最终日志
            ApiLogDTO finalLogDTO = buildApiLogDTO(joinPoint, apiLog, startTime);
            finalLogDTO.setDuration(endTime - startTime);

            if (exception != null) {
                finalLogDTO.setStatus("FAILED");
                finalLogDTO.setErrorMessage(exception.getMessage());
                finalLogDTO.setErrorStack(getStackTrace(exception));
            } else {
                finalLogDTO.setStatus("SUCCESS");
                if (result instanceof com.ly.app.common.model.Result) {
                    com.ly.app.common.model.Result<?> apiResult = (com.ly.app.common.model.Result<?>) result;
                    finalLogDTO.setResponseCode(apiResult.getCode());
                }
            }

            finalLogDTO.setCreateTime(LocalDateTime.now());

            // 打印日志
            printLog(finalLogDTO);
        }
    }


    /**
     * 构建API日志DTO
     */
    private ApiLogDTO buildApiLogDTO(JoinPoint joinPoint, ApiLog apiLog, long startTime) {
        HttpServletRequest request = null;
        try {
            request = ServletUtil.getRequest();
        } catch (Exception e) {
            log.debug("Failed to get HttpServletRequest", e);
        }

        ApiLogDTO.ApiLogDTOBuilder builder = ApiLogDTO.builder();

        if (apiLog != null) {
            builder.module(apiLog.module())
                   .description(apiLog.description());
        }

        if (request != null) {
            builder.method(request.getMethod())
                   .url(request.getRequestURI())
                   .ipAddress(ServletUtil.getClientIp(request))
                   .requestParams(getRequestParams(request))
                   .requestBody(ServletUtil.getBody(request));
        }

        // 获取当前登录用户信息
        if (StpUtil.isLogin()) {
            try {
                Object loginId = StpUtil.getLoginId();
                if (loginId != null) {
                    builder.userId(Long.parseLong(loginId.toString()));
                }
            } catch (Exception e) {
                log.debug("Failed to get userId from StpUtil", e);
            }
        }

        return builder.build();
    }

    /**
     * 处理响应数据
     */
    private void handleResponse(ApiLogDTO logDTO, Object result) {
        if (result != null) {
            try {
                if (result instanceof com.ly.app.common.model.Result) {
                    com.ly.app.common.model.Result<?> apiResult = (com.ly.app.common.model.Result<?>) result;
                    logDTO.setResponseCode(apiResult.getCode());
                    logDTO.setResponseData(JSONUtil.toJsonStr(apiResult.getData()));
                }
            } catch (Exception e) {
                log.debug("Failed to handle response data", e);
            }
        }
    }


    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            String queryString = request.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                return queryString;
            }
        } catch (Exception e) {
            log.debug("Failed to get request params", e);
        }
        return null;
    }

    /**
     * 获取异常堆栈信息
     */
    private String getStackTrace(Exception exception) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
            log.debug("Failed to get stack trace", e);
            return exception.getMessage();
        }
    }

    /**
     * 打印日志到控制台和日志文件
     */
    private void printLog(ApiLogDTO logDTO) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n")
                .append("========== API 请求响应日志 ==========\n")
                .append("模块: ").append(logDTO.getModule()).append("\n")
                .append("功能: ").append(logDTO.getDescription()).append("\n")
                .append("请求方法: ").append(logDTO.getMethod()).append("\n")
                .append("请求URL: ").append(logDTO.getUrl()).append("\n")
                .append("客户端IP: ").append(logDTO.getIpAddress()).append("\n");

        if (logDTO.getUserId() != null) {
            logBuilder.append("用户ID: ").append(logDTO.getUserId()).append("\n");
        }

        if (logDTO.getRequestParams() != null) {
            logBuilder.append("请求参数: ").append(logDTO.getRequestParams()).append("\n");
        }

        if (logDTO.getRequestBody() != null) {
            logBuilder.append("请求体: ").append(logDTO.getRequestBody()).append("\n");
        }

        logBuilder.append("响应码: ").append(logDTO.getResponseCode()).append("\n")
                .append("响应状态: ").append(logDTO.getStatus()).append("\n");

        if (logDTO.getResponseData() != null) {
            logBuilder.append("响应数据: ").append(logDTO.getResponseData()).append("\n");
        }

        if (logDTO.getErrorMessage() != null) {
            logBuilder.append("异常信息: ").append(logDTO.getErrorMessage()).append("\n");
        }

        logBuilder.append("执行时长: ").append(logDTO.getDuration()).append("ms\n")
                .append("创建时间: ").append(logDTO.getCreateTime()).append("\n")
                .append("=====================================\n");

        // 根据执行状态打印不同级别的日志
        if ("SUCCESS".equals(logDTO.getStatus())) {
            log.info(logBuilder.toString());
        } else {
            log.error(logBuilder.toString());
            if (logDTO.getErrorStack() != null) {
                log.error("异常堆栈:\n{}", logDTO.getErrorStack());
            }
        }
    }
}
