package com.ly.app.common.enums.error;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/6/14 15:33
 * @email liuyia2022@163.com
 */
public enum SpaceEnum implements ErrorType{
    SPACE_ALREADY_EXIST("空间已存在","Space already exists"),
    SPACE_NOT_EXIST("空间不存在","Space not exists"),
    SPACE_NAME_TOO_LONG("空间名过长","Space name exceeds limit"),
    SPACE_ID_NOT_MATCH("空间 id 不匹配","Space id not match"),
    SPACE_LEVEL_NOT_EXIST("空间等级不存在","Space level not exists"),
    SPACE_SIZE_TOO_LONG("空间大小超出限制","Space size exceeds limit"),
    SPACE_IMAGE_COUNT_TOO_LONG("空间图片数量超出限制","Space image count exceeds limit"),
    SPACE_TYPE_NOT_EXIST("空间类型不存在", "SpaceType not exists"),
    SPACE_ROLE_NOT_EXIST("空间用户角色不存在", "Space user role not exists"),
    SPACE_USER_NOT_EXIST("空间用户不存在", "Space User not exists");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;
    SpaceEnum(String cnMessage, String usMessage) {
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
