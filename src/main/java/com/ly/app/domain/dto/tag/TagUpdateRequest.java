package com.ly.app.domain.dto.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 标签更新请求
 * @author admin
 */
@Data
@ApiModel("标签更新请求")
public class TagUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签ID", required = true)
    @NotNull(message = "标签ID不能为空")
    private Long id;

    @ApiModelProperty(value = "标签名称")
    @Size(min = 1, max = 50, message = "标签名称长度在1-50字符之间")
    private String name;

    @ApiModelProperty(value = "标签slug（URL友好）")
    @Size(min = 1, max = 50, message = "标签slug长度在1-50字符之间")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "标签slug只能包含小写字母、数字和连字符")
    private String slug;

    @ApiModelProperty(value = "标签颜色")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "标签颜色必须是有效的十六进制颜色值")
    private String color;
}
