package com.ly.app.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 代码片段
 * @TableName code_snippet
 */
@TableName(value ="code_snippet")
@Data
public class CodeSnippet implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 代码内容
     */
    private String code;

    /**
     * 编程语言（JavaScript, TypeScript, Java, Python等）
     */
    private String language;

    /**
     * 编辑器主题（vs-dark, vs, hc-black）
     */
    private String theme;

    /**
     * 简短描述
     */
    private String description;

    /**
     * Markdown格式的详细描述
     */
    private String descriptionMarkdown;

    /**
     * 过期时间
     */
    private LocalDateTime expiryDate;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}