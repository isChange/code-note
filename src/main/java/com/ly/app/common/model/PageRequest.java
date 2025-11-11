package com.ly.app.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description TODO
 * @createDate：2025/5/27 23:02
 * @email liuyia2022@163.com
 */
@Data
public class PageRequest {
    @ApiModelProperty(value = "当前页")
    private int current = 1;

    @ApiModelProperty(value = "页面大小")
    private int pageSize = 10;

    @ApiModelProperty(value = "总数量")
    private int total = 0;

    @ApiModelProperty(value = "排序列")
    private String sortField;

    @ApiModelProperty(value = "排序的方向desc或者asc")
    private String sortOrder = "descend";


}
