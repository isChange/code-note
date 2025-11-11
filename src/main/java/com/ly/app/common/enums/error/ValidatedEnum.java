package com.ly.app.common.enums.error;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum ValidatedEnum implements ErrorType {

    //临时对象，避免模板提示信息中占位符被使用，因枚举对象属于全局对象
    TEMP("Temp", "", ""),
    NULL("Null", "%s 参数值必须是空.", "The %s parameter value must be null."),
    NOT_NULL("NotNull", "%s 参数为空.", "The %s parameter is empty."),
    NOT_BLANK("NotBlank", "%s 参数为空.", "The %s parameter is empty."),
    NOT_EMPTY("NotEmpty", "%s 参数为空.", "The %s parameter is empty."),
    MIN("Min", "%s 参数必须大于等于 %s.", "The %s parameter must be greater than or equal to %s."),
    MAX("Max", "%s 参数必须小于等于 %s.", "The %s parameter must be less than or equal to %s."),
    SIZE("Size", "%s 参数字符长度必须不大于 %s 且不小于 %s.", "The character length of the %s parameter must not be greater than %s and not less than %s."),

    REQUEST_PARAM("RequestParam", "%s 参数为空.", "The %s parameter is empty."),
    ;

    private Integer code;

    private String type;

    private String cnMessage;

    private String usMessage;

    ValidatedEnum(String type, String cnMessage, String usMessage) {
        this.code = ErrorCode.PARAMS_ERROR.getCode();
        this.type = type;
        this.cnMessage = cnMessage;
        this.usMessage = usMessage;
    }


    private ValidatedEnum setType(String type) {
        this.type = type;
        return this;
    }

    private ValidatedEnum setCnMessage(String cnMessage) {
        this.cnMessage = cnMessage;
        return this;
    }

    private ValidatedEnum setUsMessage(String usMessage) {
        this.usMessage = usMessage;
        return this;
    }

    /**
     * Description: 通过验证成员变量错误信息获取对应枚举对象并合成信息
     *
     * @param fieldError
     * @date: 2024-08-16 09:41
     */
    public static ValidatedEnum getInstanceByFieldError(FieldError fieldError, String field) {
        ValidatedEnum temp = TEMP;
        if (Objects.nonNull(field)) {
            return temp.setType(ValidatedEnum.REQUEST_PARAM.getType())
                    .setCnMessage(String.format(ValidatedEnum.REQUEST_PARAM.getCnMessage(), field))
                    .setUsMessage(String.format(ValidatedEnum.REQUEST_PARAM.getUsMessage(), field));
        }

        ValidatedEnum[] validatedEnums = values();
        for (ValidatedEnum validate : validatedEnums) {
            if (Objects.equals(validate.getType(), fieldError.getCode())) {
                //获取到code注解的值，数组个数一定要与占位符数相同
                if (Objects.equals(validate, ValidatedEnum.SIZE)) {
                    List<Object> list = Arrays.asList(fieldError.getArguments());
                    Integer v1 = (Integer) list.get(1);
                    Integer v2 = (Integer) list.get(2);
                    v1 = v1 > v2 ? v2 : v1;
                    v2 = v1 > v2 ? v1 : v2;
                    temp = temp.setType(validate.getType())
                            .setCnMessage(String.format(validate.getCnMessage(), fieldError.getField(), v1, v2))
                            .setUsMessage(String.format(validate.getUsMessage(), fieldError.getField(), v1, v2));
                } else {
                    temp = temp.setType(validate.getType())
                            .setCnMessage(String.format(validate.getCnMessage(), fieldError.getField()))
                            .setUsMessage(String.format(validate.getUsMessage(), fieldError.getField()));
                }

            }
        }
        return temp;
    }


}
