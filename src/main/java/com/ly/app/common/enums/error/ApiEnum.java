package com.ly.app.common.enums.error;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/6/16 09:21
 * @email liuyia2022@163.com
 */
public enum ApiEnum implements ErrorType{
    API_OPERATION_FAIL("接口调用失败","Api call fail"),
    PARAMS_EMPTY("参数为空","Params is empty"),
    API_AI_OUT_PAINTING_FAIL("AI 扩图失败","AI OutPainting fail"),
    API_RETURN_NOT_VALID("未返回有效结果","No valid results returned");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;
    ApiEnum(String cnMessage, String usMessage) {
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
