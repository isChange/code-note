package com.ly.app.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交注解
 * 使用 Redis 实现接口防抖，防止用户短时间内重复提交表单
 *
 * @author liu yi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventRepeatSubmit {

    /**
     * 防抖时间间隔，单位：秒
     * 默认 5 秒内不允许重复提交
     */
    int interval() default 5;

    /**
     * 提示信息
     */
    String message() default "操作过于频繁，请稍后再试";

    /**
     * 是否需要用户登录
     * 如果为 true，则使用用户ID作为唯一标识
     * 如果为 false，则使用 IP 地址作为唯一标识
     */
    boolean needLogin() default true;
}