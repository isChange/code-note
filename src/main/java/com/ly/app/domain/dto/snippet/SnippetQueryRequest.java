package com.ly.app.domain.dto.snippet;

import com.ly.app.common.model.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 代码片段查询请求
 * @author admin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("代码片段查询请求")
public class SnippetQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "代码片段ID")
    private Long id;

    @ApiModelProperty(value = "创建用户ID")
    private Long userId;

    @ApiModelProperty(value = "搜索关键词（搜索标题、描述、代码）")
    private String searchText;

    @ApiModelProperty(value = "编程语言")
    private String language;

    @ApiModelProperty(value = "标签ID列表（满足任一标签即可）")
    private List<Long> tagIds;

    @ApiModelProperty(value = "是否公开（0-私有，1-公开）")
    private Integer isPublic;

    @ApiModelProperty(value = "排序字段（createTime, updateTime, viewCount, likeCount, favoriteCount）")
    private String sortField;

    @ApiModelProperty(value = "排序方式（asc, desc）")
    private String sortOrder;
}
