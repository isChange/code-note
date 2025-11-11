package com.ly.app.domain.dto.snippet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码片段更新请求
 * @author admin
 */
@Data
@ApiModel("代码片段更新请求")
public class SnippetUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代码片段ID", required = true)
    @NotNull(message = "代码片段ID不能为空")
    private Long id;

    @ApiModelProperty(value = "标题")
    @Size(min = 1, max = 200, message = "标题长度在1-200字符之间")
    private String title;

    @ApiModelProperty(value = "代码内容")
    @Size(max = 102400, message = "代码内容不能超过100KB")
    private String code;

    @ApiModelProperty(value = "编程语言")
    private String language;

    @ApiModelProperty(value = "编辑器主题")
    private String theme;

    @ApiModelProperty(value = "简短描述")
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    @ApiModelProperty(value = "Markdown格式的详细描述")
    private String descriptionMarkdown;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expiryDate;

    @ApiModelProperty(value = "是否公开（0-私有，1-公开）")
    private Integer isPublic;

    @ApiModelProperty(value = "标签ID列表")
    @Size(max = 10, message = "最多只能选择10个标签")
    private List<Long> tagIds;
}
