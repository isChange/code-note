package com.ly.app.domain.dto.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API请求响应日志DTO
 *
 * @author liu yi
 * @version v1.0.0
 * @Description 记录API请求和响应的详细信息
 * @createDate 2025/11/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLogDTO {

    /**
     * 功能模块
     */
    private String module;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求IP地址
     */
    private String ipAddress;

    /**
     * 请求参数（JSON格式）
     */
    private String requestParams;

    /**
     * 请求体（JSON格式）
     */
    private String requestBody;

    /**
     * 响应码
     */
    private Integer responseCode;

    /**
     * 响应数据（JSON格式）
     */
    private String responseData;

    /**
     * 执行时长（毫秒）
     */
    private Long duration;

    /**
     * 执行状态（成功/失败）
     */
    private String status;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 异常堆栈
     */
    private String errorStack;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
