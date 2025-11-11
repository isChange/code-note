package com.ly.app.common.enums.error;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/28 17:21
 * @email liuyia2022@163.com
 */
public interface ErrorType {
    /**
     * 返回code
     *
     * @return
     */
    Integer getCode();

    /**
     * 返回中文 message
     *
     * @return
     */
    String getCnMessage();


    /**
     * 返回英文 message
     *
     * @return
     */
    String getUsMessage();
}
