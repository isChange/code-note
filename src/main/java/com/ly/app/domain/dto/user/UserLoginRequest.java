package com.ly.app.domain.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/31 17:58
 * @email liuyia2022@163.com
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7258459233470304379L;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    private String account;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度在6-20位之间")
    private String password;
}
