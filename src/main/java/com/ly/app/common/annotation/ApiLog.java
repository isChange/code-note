package com.ly.app.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API日志记录注解
 *
 * @author liu yi
 * @version v1.0.0
 * @Description 用于标记需要记录请求响应日志的接口方法
 * @createDate 2025/11/9
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {

    /**
     * 功能模块（必填）
     * 例如：用户管理、代码片段、空间管理
     */
    String module() default "";

    /**
     * 功能描述（必填）
     * 例如：用户登录、创建代码片段、删除空间
     */
    String description() default "";
}
