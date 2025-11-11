package com.ly.app.domain.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/30 19:01
 * @email liuyia2022@163.com
 */
@Data
public class SafeUserVO implements Serializable {

    private static final long serialVersionUID = -1102172287278378133L;
    @ApiModelProperty ("id")
    private Long id;
    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String userAccount;
    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户头像")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty("用户简介")
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    @ApiModelProperty("用户角色")
    private String userRole;
    /**
     * 编辑时间
     */
    @ApiModelProperty("编辑时间")
    private Date editTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

}
