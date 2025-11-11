package com.ly.app.common.enums.error;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/30 19:56
 * @email liuyia2022@163.com
 */
public enum UserEnum implements ErrorType {
    USER_PASSWORD_NOT_EQUAL("密码与确认密码不一致","Password and confirmation password do not match"),
    USER_EXIST("用户已存在", "The user already exists"),
    USER_NOT_EXIST("用户不存在", "The user not exists"),
    USER_LOGIN_FAIL("账户或密码错误", "Account or password error"),
    USER_PASSWORD_INVALID("密码不合法", "Password is invalid");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;
    UserEnum(String usMessage, String cnMessage) {
        this.code = ErrorCode.OPERATION_ERROR.getCode();
        this.usMessage  = usMessage;
        this.cnMessage = cnMessage;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getCnMessage() {
        return this.cnMessage;
    }

    @Override
    public String getUsMessage() {
        return this.usMessage;
    }
}
