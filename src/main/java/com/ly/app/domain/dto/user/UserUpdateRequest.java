package com.ly.app.domain.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/31 19:01
 * @email liuyia2022@163.com
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 180486979526718710L;
    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "用户昵称")
    private String userName;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "用户简介")
    private String userProfile;
}
