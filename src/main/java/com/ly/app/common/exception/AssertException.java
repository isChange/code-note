package com.ly.app.common.exception;

import com.ly.app.common.enums.error.ErrorType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/29 10:10
 * @email liuyia2022@163.com
 */
@Getter
@Setter
public class AssertException extends BaseException{
    public AssertException(ErrorType errorType) {
        super(errorType);
    }
}
