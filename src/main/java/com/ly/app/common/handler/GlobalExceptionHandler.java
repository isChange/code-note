package com.ly.app.common.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.enums.error.ValidatedEnum;
import com.ly.app.common.exception.AssertException;
import com.ly.app.common.exception.BaseException;
import com.ly.app.common.model.Result;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.SignatureException;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author liu yi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest request;

    /**
     * Spring 常用异常
     */
    @ExceptionHandler(value = {ResponseStatusException.class})
    public Result handle(ResponseStatusException ex) {
        log.error("请求地址: {}; 系统状态响应异常为:", request.getRequestURI(), ex);
        return Result.fail(ErrorCode.GATEWAY_ERROR);
    }

    @ExceptionHandler(value = {SignatureException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result handle(SignatureException ex) {
        log.error("请求地址: {}; 签名认证拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.PARAM_TOKEN);
    }

    @ExceptionHandler(value = {ConnectTimeoutException.class})
    public Result handle(ConnectTimeoutException ex) {
        log.error("请求地址: {}; 连接超时拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.GATEWAY_TIME_OUT);
    }

    @ExceptionHandler(value = {MultipartException.class})
    public Result uploadFileLimitException(MultipartException ex) {
        log.error("请求地址: {}; 附件上传限制拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.PARAM_FILE_SIZE);
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public Result duplicateKeyException(DuplicateKeyException ex) {
        log.error("请求地址: {}; 唯一键冲突拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.SQL_PRIMARY_KEY);
    }

    /**
     * 参数校验异常
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Result methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (Objects.nonNull(fieldError)) {
            return Result.fail(ValidatedEnum.getInstanceByFieldError(fieldError, null));
        }
        return Result.fail();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public Result bindException(BindException ex) {
        FieldError fieldError = ex.getFieldError();
        if (Objects.nonNull(fieldError)) {
            return Result.fail(ValidatedEnum.getInstanceByFieldError(fieldError, null));
        }
        return Result.fail();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public Result missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return Result.fail(ValidatedEnum.getInstanceByFieldError(null, ex.getParameterName()));
    }

    /**
     * SaToken 异常处理
     */
    @ExceptionHandler(NotLoginException.class)
    public Result notLoginException(NotLoginException ex) {
        log.error("请求地址: {}; 用户未登录异常: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.NOT_LOGIN_ERROR, "用户未登录，请先登录");
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result notPermissionException(NotPermissionException ex) {
        log.error("请求地址: {}; 权限不足异常: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.NO_AUTH_ERROR, "权限不足");
    }

    @ExceptionHandler(NotRoleException.class)
    public Result notRoleException(NotRoleException ex) {
        log.error("请求地址: {}; 角色权限不足异常: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ErrorCode.NO_AUTH_ERROR, "角色权限不足");
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exception(Exception ex) {
        log.error("请求地址: {}; 系统全局统一异常为:", request.getRequestURI(), ex);
        return Result.fail();
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result throwable(Throwable t) {
        log.error("请求地址: {}; 系统全局抛出异常为:", request.getRequestURI(), t);
        return Result.fail();
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = {BaseException.class})
    public Result baseException(BaseException ex) {
        log.error("请求地址: {}; 自定义基础拦截异常: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ex.getErrorType());
    }

    @ExceptionHandler(value = {AssertException.class})
    public Result alertException(AssertException ex) {
        log.error("请求地址: {}; Assert参数拦截异常为: {}", request.getRequestURI(), ex.getMessage());
        return Result.fail(ex.getErrorType());
    }
}
