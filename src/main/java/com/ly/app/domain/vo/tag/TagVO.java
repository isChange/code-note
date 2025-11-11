package com.ly.app.domain.vo.tag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签视图对象
 * @author admin
 */
@Data
@ApiModel("标签视图对象")
public class TagVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签ID")
    private Long id;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("标签slug")
    private String slug;

    @ApiModelProperty("标签颜色")
    private String color;

    @ApiModelProperty("使用次数")
    private Integer useCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
