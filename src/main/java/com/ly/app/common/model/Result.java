package com.ly.app.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ly.app.common.constant.SysConstant;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.enums.error.ErrorType;
import com.ly.app.common.units.ServletUtil;
import lombok.*;


import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:01
 * @email liuyia2022@163.com
 */
@Getter
@ToString
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 7505175748532929242L;
    private Integer code;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant time;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    public Result() {
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * @param type
     * @param data
     */
    public Result(ErrorType type, T data) {
        this(type);
        this.data = data;
    }

    /**
     * @param errorType
     */
    public Result(ErrorType errorType) {
        String language = ServletUtil.getHeader(SysConstant.LANGUAGE);
        if (Objects.equals(language, SysConstant.LANGUAGE_EN_US)) {
            this.message = errorType.getUsMessage();
        } else {
            this.message = errorType.getCnMessage();
        }
        this.code = errorType.getCode();
        this.time = ZonedDateTime.now().toInstant();
    }

    /**
     * 快速创建成功结果
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 快速创建成功结果并返回结果数据
     */
    public static Result success(Object data) {
        return new Result<>(ErrorCode.SUCCESS, data);
    }

    /**
     * 系统异常类没有返回数据
     */
    public static Result fail() {
        return new Result(ErrorCode.OPERATION_ERROR);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(ErrorType errorType) {
        return Result.fail(errorType, null);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(Object data) {
        return new Result<>(ErrorCode.OPERATION_ERROR, data);
    }

    /**
     * 系统异常类并返回结果数据
     */
    public static Result fail(ErrorType errorType, Object data) {
        return new Result<>(errorType, data);
    }

    /**
     * 成功
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(ErrorCode.SUCCESS.getCode(), this.code);
    }

    /**
     * 失败
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
