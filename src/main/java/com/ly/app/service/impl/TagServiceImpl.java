package com.ly.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.app.common.annotation.ApiLog;
import com.ly.app.common.cache.RedisCache;
import com.ly.app.common.constant.RedisConstant;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.units.AssertUtil;
import com.ly.app.domain.dto.tag.TagCreateRequest;
import com.ly.app.domain.dto.tag.TagQueryRequest;
import com.ly.app.domain.dto.tag.TagUpdateRequest;
import com.ly.app.domain.entity.SnippetTag;
import com.ly.app.domain.entity.Tag;
import com.ly.app.domain.vo.tag.TagVO;
import com.ly.app.mapper.SnippetTagMapper;
import com.ly.app.mapper.TagMapper;
import com.ly.app.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 针对表【tag(标签)】的数据库操作Service实现
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

    @Resource
    private SnippetTagMapper snippetTagMapper;

    @Override
    public Long createTag(TagCreateRequest createRequest) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tag> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(Tag::getName, createRequest.getName());
        Long nameCount = this.count(nameWrapper);
        AssertUtil.isTrue(nameCount == 0, ErrorCode.PARAMS_ERROR);

        // 检查标签slug是否已存在
        LambdaQueryWrapper<Tag> slugWrapper = new LambdaQueryWrapper<>();
        slugWrapper.eq(Tag::getSlug, createRequest.getSlug());
        Long slugCount = this.count(slugWrapper);
        AssertUtil.isTrue(slugCount == 0, ErrorCode.PARAMS_ERROR);

        // 创建标签
        Tag tag = new Tag();
        BeanUtils.copyProperties(createRequest, tag);
        tag.setUseCount(0);

        boolean saved = this.save(tag);
        AssertUtil.isTrue(saved, ErrorCode.OPERATION_ERROR);
        //删除缓存
        RedisCache.removeObject(RedisConstant.ALL_TAG);
        return tag.getId();
    }

    @Override
    public Boolean updateTag(TagUpdateRequest updateRequest) {
        // 检查标签是否存在
        Tag tag = this.getById(updateRequest.getId());
        AssertUtil.isNotNull(tag, ErrorCode.NOT_FOUND_ERROR);

        // 如果要修改名称，检查新名称是否已被其他标签使用
        if (StrUtil.isNotBlank(updateRequest.getName()) && !updateRequest.getName().equals(tag.getName())) {
            LambdaQueryWrapper<Tag> nameWrapper = new LambdaQueryWrapper<>();
            nameWrapper.eq(Tag::getName, updateRequest.getName())
                    .ne(Tag::getId, updateRequest.getId());
            Long nameCount = this.count(nameWrapper);
            AssertUtil.isTrue(nameCount == 0, ErrorCode.PARAMS_ERROR);
        }

        // 如果要修改slug，检查新slug是否已被其他标签使用
        if (StrUtil.isNotBlank(updateRequest.getSlug()) && !updateRequest.getSlug().equals(tag.getSlug())) {
            LambdaQueryWrapper<Tag> slugWrapper = new LambdaQueryWrapper<>();
            slugWrapper.eq(Tag::getSlug, updateRequest.getSlug())
                    .ne(Tag::getId, updateRequest.getId());
            Long slugCount = this.count(slugWrapper);
            AssertUtil.isTrue(slugCount == 0, ErrorCode.PARAMS_ERROR);
        }

        // 更新标签
        BeanUtils.copyProperties(updateRequest, tag);
        tag.setId(updateRequest.getId());
        boolean updated = this.updateById(tag);
        AssertUtil.isTrue(updated, ErrorCode.OPERATION_ERROR);
        //删除缓存
        RedisCache.removeObject(RedisConstant.ALL_TAG);
        return true;
    }

    @Override
    public Boolean deleteTag(Long id) {
        // 检查标签是否存在
        Tag tag = this.getById(id);
        AssertUtil.isNotNull(tag, ErrorCode.NOT_FOUND_ERROR);

        // 检查标签是否被使用
        LambdaQueryWrapper<SnippetTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnippetTag::getTagId, id);
        Long count = snippetTagMapper.selectCount(wrapper);
        AssertUtil.isTrue(count == 0, ErrorCode.OPERATION_ERROR);

        // 删除标签
        boolean deleted = this.removeById(id);
        AssertUtil.isTrue(deleted, ErrorCode.OPERATION_ERROR);
        //删除缓存
        RedisCache.removeObject(RedisConstant.ALL_TAG);
        return true;
    }

    @Override
    public TagVO getTagById(Long id) {
        Tag tag = this.getById(id);
        AssertUtil.isNotNull(tag, ErrorCode.NOT_FOUND_ERROR);

        return convertToVO(tag);
    }

    @Override
    public IPage<TagVO> pageTags(TagQueryRequest queryRequest) {
        // 构建查询条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();

        // ID查询
        wrapper.eq(queryRequest.getId() != null, Tag::getId, queryRequest.getId());

        // 名称查询
        wrapper.like(StrUtil.isNotBlank(queryRequest.getName()), Tag::getName, queryRequest.getName());

        // slug查询
        wrapper.eq(StrUtil.isNotBlank(queryRequest.getSlug()), Tag::getSlug, queryRequest.getSlug());

        // 排序
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (StrUtil.isNotBlank(sortField)) {
            boolean isAsc = "asc".equals(sortOrder);
            switch (sortField) {
                case "useCount":
                    wrapper.orderBy(true, isAsc, Tag::getUseCount);
                    break;
                case "createTime":
                    wrapper.orderBy(true, isAsc, Tag::getCreateTime);
                    break;
                default:
                    wrapper.orderByDesc(Tag::getUseCount);
            }
        } else {
            wrapper.orderByDesc(Tag::getUseCount);
        }

        // 分页查询
        Page<Tag> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
        Page<Tag> tagPage = this.page(page, wrapper);

        // 转换为VO
        Page<TagVO> voPage = new Page<>(tagPage.getCurrent(), tagPage.getSize(), tagPage.getTotal());
        List<TagVO> voList = tagPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @ApiLog(module = "标签模块", description = "获取所有标签")
    public List<TagVO> listAllTags() {
        //取缓存
        List<Object> list = RedisCache.getList(RedisConstant.ALL_TAG);
        if (CollectionUtil.isNotEmpty(list)){
            return list.stream().map(o -> convertToVO((Tag) o)).collect(Collectors.toList());
        }
        //缓存没有取数据数据库
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getUseCount);
        List<Tag> tags = this.list(wrapper);
        //更新缓存
        RedisCache.setList(RedisConstant.ALL_TAG, tags);
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> listTagsBySnippetId(Long snippetId) {
        // 查询代码片段-标签关联
        LambdaQueryWrapper<SnippetTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnippetTag::getSnippetId, snippetId);
        List<SnippetTag> snippetTags = snippetTagMapper.selectList(wrapper);

        if (snippetTags.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取标签ID列表
        List<Long> tagIds = snippetTags.stream()
                .map(SnippetTag::getTagId)
                .collect(Collectors.toList());

        // 查询标签
        List<Tag> tags = this.listByIds(tagIds);

        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<TagVO>> listTagsBySnippetIds(List<Long> snippetIds) {
        if (snippetIds == null || snippetIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询所有相关的代码片段-标签关联
        LambdaQueryWrapper<SnippetTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SnippetTag::getSnippetId, snippetIds);
        List<SnippetTag> snippetTags = snippetTagMapper.selectList(wrapper);

        if (snippetTags.isEmpty()) {
            return new HashMap<>();
        }

        // 获取所有相关的标签
        List<Long> tagIds = snippetTags.stream()
                .map(SnippetTag::getTagId)
                .distinct()
                .collect(Collectors.toList());
        List<Tag> tags = this.listByIds(tagIds);

        // 构建标签ID到标签VO的映射
        Map<Long, TagVO> tagMap = tags.stream()
                .collect(Collectors.toMap(Tag::getId, this::convertToVO));

        // 构建代码片段ID到标签列表的映射
        Map<Long, List<TagVO>> result = new HashMap<>();
        for (SnippetTag snippetTag : snippetTags) {
            Long snippetId = snippetTag.getSnippetId();
            TagVO tagVO = tagMap.get(snippetTag.getTagId());
            if (tagVO != null) {
                result.computeIfAbsent(snippetId, k -> new ArrayList<>()).add(tagVO);
            }
        }

        return result;
    }

    /**
     * 转换为VO对象
     */
    private TagVO convertToVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
}
