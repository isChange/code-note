package com.ly.app.domain.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/31 18:51
 * @email liuyia2022@163.com
 */
@Data
public class UserCreateRequest implements Serializable {
    private static final long serialVersionUID = -7131481744661848814L;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "账号")
    private String userPassword;

    @ApiModelProperty(value = "用户昵称")
    private String userName;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "用户简介")
    private String userProfile;

    @ApiModelProperty(value = "用户角色")
    private String userRole;
}
