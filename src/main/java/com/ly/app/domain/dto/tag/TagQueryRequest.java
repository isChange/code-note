package com.ly.app.domain.dto.tag;

import com.ly.app.common.model.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 标签查询请求
 * @author admin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("标签查询请求")
public class TagQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签ID")
    private Long id;

    @ApiModelProperty(value = "标签名称")
    private String name;

    @ApiModelProperty(value = "标签slug")
    private String slug;

    @ApiModelProperty(value = "排序字段（useCount, createTime）")
    private String sortField;

    @ApiModelProperty(value = "排序方式（asc, desc）")
    private String sortOrder;
}
