package com.ly.app.domain.dto.user;

import com.ly.app.common.model.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/31 18:43
 * @email liuyia2022@163.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 7396712053204643142L;
    @ApiModelProperty(value = "账号")
    private String userAccount;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "用户角色")
    private String userRole;
    @ApiModelProperty(value = "用户简介")
    private String userProfile;
}
