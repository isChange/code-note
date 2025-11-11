package com.ly.app.common.enums.error;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/6/8 19:38
 * @email liuyia2022@163.com
 */
public enum PictureEnum implements ErrorType {
    PICTURE_NOT_EXIST("图片不存在","Picture not exists"),
    PICTURE_EXIST("图片已存在","Picture already exists"),
    PICTURE_FETCH_ERROR("图片抓取异常","Picture fetch error"),
    PICTURE_REVIEW_NO_PASS("图片未过审","The image has not been approved"),
    PICTURE_REVIEW_STATUS_NOT_REVIEWING("图片不处于待审核状态","The image is not in the pending review status"),
    PICTURE_REVIEW_STATUS_NOT_EXIST("审核状态不存在","Picture review status not exists"),
    PICTURE_REPLACE_REVIEW("请勿重复审核图片","Please do not review images repeatedly"),
    PICTURE_INTRODUCTION_TOO_LONG("图片简介长于800","The image introduction is longer than 800"),
    PICTURE_URL_TOO_LONG("图片url长于1024","The image URL is longer than 1024");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String usMessage;
    private final String cnMessage;
    PictureEnum(String usMessage, String cnMessage) {
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
