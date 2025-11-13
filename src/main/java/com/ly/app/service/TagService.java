package com.ly.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.app.domain.entity.Tag;
import com.ly.app.domain.dto.tag.TagCreateRequest;
import com.ly.app.domain.dto.tag.TagQueryRequest;
import com.ly.app.domain.dto.tag.TagUpdateRequest;
import com.ly.app.domain.vo.tag.TagVO;

import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @description 针对表【tag(标签)】的数据库操作Service
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     * @param createRequest 创建请求
     * @return 标签ID
     */
    Long createTag(TagCreateRequest createRequest);

    /**
     * 更新标签
     * @param updateRequest 更新请求
     * @return 是否成功
     */
    Boolean updateTag(TagUpdateRequest updateRequest);

    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否成功
     */
    Boolean deleteTag(Long id);

    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签VO
     */
    TagVO getTagById(Long id);

    /**
     * 分页查询标签
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    IPage<TagVO> pageTags(TagQueryRequest queryRequest);

    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<TagVO> listAllTags();

    /**
     * 根据代码片段ID获取标签列表
     * @param snippetId 代码片段ID
     * @return 标签列表
     */
    List<TagVO> listTagsBySnippetId(Long snippetId);

    /**
     * 根据多个代码片段ID获取标签列表（批量）
     * @param snippetIds 代码片段ID列表
     * @return 标签列表Map（key为snippetId，value为标签列表）
     */
    Map<Long, List<TagVO>> listTagsBySnippetIds(List<Long> snippetIds);

    /**
     * 根据用户Id汇总标签情况
     * @param userId 用户id
     * @param isFavorite 是否收藏
     * @return
     */
    List<TagVO> listTagsByUserId(Long userId, boolean isFavorite);

}
