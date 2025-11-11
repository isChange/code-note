package com.ly.app.domain.vo.snippet;

import com.ly.app.domain.vo.tag.TagVO;
import com.ly.app.domain.vo.user.SafeUserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 代码片段视图对象
 * @author admin
 */
@Data
@ApiModel("代码片段视图对象")
public class CodeSnippetVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("代码片段ID")
    private Long id;

    @ApiModelProperty("创建用户ID")
    private Long userId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("代码内容")
    private String code;

    @ApiModelProperty("编程语言")
    private String language;

    @ApiModelProperty("编辑器主题")
    private String theme;

    @ApiModelProperty("简短描述")
    private String description;

    @ApiModelProperty("Markdown格式的详细描述")
    private String descriptionMarkdown;

    @ApiModelProperty("过期时间")
    private LocalDateTime expiryDate;

    @ApiModelProperty("是否公开（0-私有，1-公开）")
    private Integer isPublic;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("收藏数")
    private Integer favoriteCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建用户信息")
    private SafeUserVO user;

    @ApiModelProperty("标签列表")
    private List<TagVO> tags;

    @ApiModelProperty("当前用户是否已点赞")
    private Boolean hasLiked;

    @ApiModelProperty("当前用户是否已收藏")
    private Boolean hasFavorited;
}
