package com.ly.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.app.domain.entity.CodeSnippet;
import com.ly.app.domain.dto.snippet.SnippetCreateRequest;
import com.ly.app.domain.dto.snippet.SnippetQueryRequest;
import com.ly.app.domain.dto.snippet.SnippetUpdateRequest;
import com.ly.app.domain.vo.snippet.CodeSnippetVO;

/**
 * @author admin
 * @description 针对表【code_snippet(代码片段)】的数据库操作Service
 */
public interface CodeSnippetService extends IService<CodeSnippet> {

    /**
     * 创建代码片段
     * @param createRequest 创建请求
     * @return 代码片段ID
     */
    Long createSnippet(SnippetCreateRequest createRequest);

    /**
     * 更新代码片段
     * @param updateRequest 更新请求
     * @return 是否成功
     */
    Boolean updateSnippet(SnippetUpdateRequest updateRequest);

    /**
     * 删除代码片段
     * @param id 代码片段ID
     * @return 是否成功
     */
    Boolean deleteSnippet(Long id);

    /**
     * 根据ID获取代码片段
     * @param id 代码片段ID
     * @return 代码片段VO
     */
    CodeSnippetVO getSnippetById(Long id);

    /**
     * 分页查询代码片段
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    IPage<CodeSnippetVO> pageSnippets(SnippetQueryRequest queryRequest);

    /**
     * 点赞代码片段
     * @param snippetId 代码片段ID
     * @return 是否成功
     */
    Boolean likeSnippet(Long snippetId);

    /**
     * 取消点赞代码片段
     * @param snippetId 代码片段ID
     * @return 是否成功
     */
    Boolean unlikeSnippet(Long snippetId);

    /**
     * 收藏代码片段
     * @param snippetId 代码片段ID
     * @return 是否成功
     */
    Boolean favoriteSnippet(Long snippetId);

    /**
     * 取消收藏代码片段
     * @param snippetId 代码片段ID
     * @return 是否成功
     */
    Boolean unfavoriteSnippet(Long snippetId);

    /**
     * 增加浏览次数
     * @param snippetId 代码片段ID
     */
    void incrementViewCount(Long snippetId);
}
