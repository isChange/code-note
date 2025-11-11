package com.ly.app.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 代码片段-标签关联
 * @TableName snippet_tag
 */
@TableName(value ="snippet_tag")
@Data
public class SnippetTag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 代码片段ID
     */
    private Long snippetId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
