package com.ly.app.domain.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/30 18:22
 * @email liuyia2022@163.com
 */
@Getter
public enum UserRoleEnum {
    USER("user", "普通用户"),
    ADMIN("admin", "管理员");
    private String value;
    private String desc;
    UserRoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserRoleEnum getRoleByKey(String role) {
        if (StrUtil.isBlank(role)){
            return null;
        }
        for (UserRoleEnum value : UserRoleEnum.values()) {
            if (value.value.equals(role)) {
                return value;
            }
        }
        return null;
    }

}
