package com.ly.app.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.exception.AssertException;
import com.ly.app.common.units.AssertUtil;
import com.ly.app.domain.dto.snippet.SnippetCreateRequest;
import com.ly.app.domain.dto.snippet.SnippetQueryRequest;
import com.ly.app.domain.dto.snippet.SnippetUpdateRequest;
import com.ly.app.domain.entity.*;
import com.ly.app.domain.vo.snippet.CodeSnippetVO;
import com.ly.app.domain.vo.tag.TagVO;
import com.ly.app.domain.vo.user.SafeUserVO;
import com.ly.app.mapper.CodeSnippetMapper;
import com.ly.app.mapper.FavoriteMapper;
import com.ly.app.mapper.SnippetLikeMapper;
import com.ly.app.mapper.SnippetTagMapper;
import com.ly.app.service.CodeSnippetService;
import com.ly.app.service.TagService;
import com.ly.app.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 针对表【code_snippet(代码片段)】的数据库操作Service实现
 */
@Service
public class CodeSnippetServiceImpl extends ServiceImpl<CodeSnippetMapper, CodeSnippet>
        implements CodeSnippetService {

    @Resource
    private SnippetTagMapper snippetTagMapper;

    @Resource
    private SnippetLikeMapper snippetLikeMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private TagService tagService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSnippet(SnippetCreateRequest createRequest) {
        // 获取当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();

        // 创建代码片段
        CodeSnippet snippet = new CodeSnippet();
        BeanUtils.copyProperties(createRequest, snippet);
        snippet.setUserId(userId);
        snippet.setViewCount(0);
        snippet.setLikeCount(0);
        snippet.setFavoriteCount(0);

        // 保存代码片段
        boolean saved = this.save(snippet);
        AssertUtil.isTrue(saved, ErrorCode.OPERATION_ERROR);

        // 关联标签
        if (createRequest.getTagIds() != null && !createRequest.getTagIds().isEmpty()) {
            saveSnippetTags(snippet.getId(), createRequest.getTagIds());
        }

        return snippet.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSnippet(SnippetUpdateRequest updateRequest) {
        // 获取当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询代码片段
        CodeSnippet snippet = this.getById(updateRequest.getId());
        AssertUtil.isNotNull(snippet, ErrorCode.NOT_FOUND_ERROR);
        AssertUtil.isTrue(snippet.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR);

        // 更新代码片段
        BeanUtils.copyProperties(updateRequest, snippet);
        snippet.setId(updateRequest.getId());
        boolean updated = this.updateById(snippet);
        AssertUtil.isTrue(updated, ErrorCode.OPERATION_ERROR);

        // 更新标签关联
        if (updateRequest.getTagIds() != null) {
            // 删除旧的标签关联
            LambdaQueryWrapper<SnippetTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SnippetTag::getSnippetId, snippet.getId());
            snippetTagMapper.delete(wrapper);

            // 添加新的标签关联
            if (!updateRequest.getTagIds().isEmpty()) {
                saveSnippetTags(snippet.getId(), updateRequest.getTagIds());
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSnippet(Long id) {
        // 获取当前登录用户
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询代码片段
        CodeSnippet snippet = this.getById(id);
        AssertUtil.isNotNull(snippet, ErrorCode.NOT_FOUND_ERROR);
        AssertUtil.isTrue(snippet.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR);

        // 删除代码片段（逻辑删除）
        boolean deleted = this.removeById(id);
        AssertUtil.isTrue(deleted, ErrorCode.OPERATION_ERROR);

        // 删除标签关联
        LambdaQueryWrapper<SnippetTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnippetTag::getSnippetId, id);
        snippetTagMapper.delete(wrapper);

        return true;
    }

    @Override
    public CodeSnippetVO getSnippetById(Long id) {
        CodeSnippet snippet = this.getById(id);
        AssertUtil.isNotNull(snippet, ErrorCode.NOT_FOUND_ERROR);

        // 检查权限
        if (snippet.getIsPublic() == 0) {
            Long userId = null;
            try {
                userId = StpUtil.getLoginIdAsLong();
            } catch (Exception e) {
                // 未登录
            }
            AssertUtil.isTrue(userId != null && snippet.getUserId().equals(userId),
                ErrorCode.NO_AUTH_ERROR);
        }

        // 转换为VO
        return convertToVO(snippet, true);
    }

    @Override
    public IPage<CodeSnippetVO> pageSnippets(SnippetQueryRequest queryRequest) {
        // 构建查询条件
        LambdaQueryWrapper<CodeSnippet> wrapper = new LambdaQueryWrapper<>();

        // ID查询
        wrapper.eq(queryRequest.getId() != null, CodeSnippet::getId, queryRequest.getId());

        // 用户ID查询
        wrapper.eq(queryRequest.getUserId() != null, CodeSnippet::getUserId, queryRequest.getUserId());

        // 编程语言查询
        wrapper.eq(StrUtil.isNotBlank(queryRequest.getLanguage()), CodeSnippet::getLanguage, queryRequest.getLanguage());

        // 公开性查询
        if (queryRequest.getIsPublic() != null) {
            wrapper.eq(CodeSnippet::getIsPublic, queryRequest.getIsPublic());
        } else {
            // 如果没有指定，默认只查询公开的或者自己的
            Long userId = null;
            try {
                userId = StpUtil.getLoginIdAsLong();
            } catch (Exception e) {
                // 未登录，只查询公开的
                wrapper.eq(CodeSnippet::getIsPublic, 1);
            }
            if (userId != null) {
                Long finalUserId = userId;
                wrapper.and(w -> w.eq(CodeSnippet::getIsPublic, 1).or().eq(CodeSnippet::getUserId, finalUserId));
            }
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(queryRequest.getSearchText())) {
            wrapper.and(w -> w.like(CodeSnippet::getTitle, queryRequest.getSearchText())
                    .or().like(CodeSnippet::getDescription, queryRequest.getSearchText())
                    .or().like(CodeSnippet::getCode, queryRequest.getSearchText()));
        }

        // 排序
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (StrUtil.isNotBlank(sortField)) {
            boolean isAsc = "asc".equals(sortOrder);
            switch (sortField) {
                case "createTime":
                    wrapper.orderBy(true, isAsc, CodeSnippet::getCreateTime);
                    break;
                case "updateTime":
                    wrapper.orderBy(true, isAsc, CodeSnippet::getUpdateTime);
                    break;
                case "viewCount":
                    wrapper.orderBy(true, isAsc, CodeSnippet::getViewCount);
                    break;
                case "likeCount":
                    wrapper.orderBy(true, isAsc, CodeSnippet::getLikeCount);
                    break;
                case "favoriteCount":
                    wrapper.orderBy(true, isAsc, CodeSnippet::getFavoriteCount);
                    break;
                default:
                    wrapper.orderByDesc(CodeSnippet::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(CodeSnippet::getCreateTime);
        }

        // 分页查询
        Page<CodeSnippet> page = new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
        Page<CodeSnippet> snippetPage = this.page(page, wrapper);

        // 转换为VO
        Page<CodeSnippetVO> voPage = new Page<>(snippetPage.getCurrent(), snippetPage.getSize(), snippetPage.getTotal());
        List<CodeSnippetVO> voList = snippetPage.getRecords().stream()
                .map(snippet -> convertToVO(snippet, false))
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeSnippet(Long snippetId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查代码片段是否存在
        CodeSnippet snippet = this.getById(snippetId);
        AssertUtil.isNotNull(snippet, ErrorCode.NOT_FOUND_ERROR);

        // 检查是否已点赞
        LambdaQueryWrapper<SnippetLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnippetLike::getUserId, userId)
                .eq(SnippetLike::getSnippetId, snippetId);
        Long count = snippetLikeMapper.selectCount(wrapper);
        AssertUtil.isTrue(count == 0, ErrorCode.OPERATION_ERROR);

        // 添加点赞记录
        SnippetLike snippetLike = new SnippetLike();
        snippetLike.setUserId(userId);
        snippetLike.setSnippetId(snippetId);
        int inserted = snippetLikeMapper.insert(snippetLike);
        AssertUtil.isTrue(inserted > 0, ErrorCode.OPERATION_ERROR);

        // 更新点赞数
        snippet.setLikeCount(snippet.getLikeCount() + 1);
        this.updateById(snippet);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeSnippet(Long snippetId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 删除点赞记录
        LambdaQueryWrapper<SnippetLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnippetLike::getUserId, userId)
                .eq(SnippetLike::getSnippetId, snippetId);
        int deleted = snippetLikeMapper.delete(wrapper);
        AssertUtil.isTrue(deleted > 0, ErrorCode.OPERATION_ERROR);

        // 更新点赞数
        CodeSnippet snippet = this.getById(snippetId);
        if (snippet != null && snippet.getLikeCount() > 0) {
            snippet.setLikeCount(snippet.getLikeCount() - 1);
            this.updateById(snippet);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean favoriteSnippet(Long snippetId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查代码片段是否存在
        CodeSnippet snippet = this.getById(snippetId);
        AssertUtil.isNotNull(snippet, ErrorCode.NOT_FOUND_ERROR);

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getSnippetId, snippetId);
        Long count = favoriteMapper.selectCount(wrapper);
        AssertUtil.isTrue(count == 0, ErrorCode.OPERATION_ERROR);

        // 添加收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setSnippetId(snippetId);
        int inserted = favoriteMapper.insert(favorite);
        AssertUtil.isTrue(inserted > 0, ErrorCode.OPERATION_ERROR);

        // 更新收藏数
        snippet.setFavoriteCount(snippet.getFavoriteCount() + 1);
        this.updateById(snippet);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unfavoriteSnippet(Long snippetId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 删除收藏记录
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getSnippetId, snippetId);
        int deleted = favoriteMapper.delete(wrapper);
        AssertUtil.isTrue(deleted > 0, ErrorCode.OPERATION_ERROR);

        // 更新收藏数
        CodeSnippet snippet = this.getById(snippetId);
        if (snippet != null && snippet.getFavoriteCount() > 0) {
            snippet.setFavoriteCount(snippet.getFavoriteCount() - 1);
            this.updateById(snippet);
        }

        return true;
    }

    @Override
    public void incrementViewCount(Long snippetId) {
        CodeSnippet snippet = this.getById(snippetId);
        if (snippet != null) {
            snippet.setViewCount(snippet.getViewCount() + 1);
            this.updateById(snippet);
        }
    }

    /**
     * 保存代码片段-标签关联
     */
    private void saveSnippetTags(Long snippetId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            SnippetTag snippetTag = new SnippetTag();
            snippetTag.setSnippetId(snippetId);
            snippetTag.setTagId(tagId);
            snippetTagMapper.insert(snippetTag);
        }
    }

    /**
     * 转换为VO对象
     */
    private CodeSnippetVO convertToVO(CodeSnippet snippet, boolean includeDetails) {
        CodeSnippetVO vo = new CodeSnippetVO();
        BeanUtils.copyProperties(snippet, vo);

        // 获取用户信息
        User user = userService.getById(snippet.getUserId());
        if (user != null) {
            SafeUserVO userVO = new SafeUserVO();
            BeanUtils.copyProperties(user, userVO);
            vo.setUser(userVO);
        }

        // 获取标签列表
        List<TagVO> tags = tagService.listTagsBySnippetId(snippet.getId());
        vo.setTags(tags);

        // 如果需要详细信息，获取当前用户的点赞和收藏状态
        if (includeDetails) {
            try {
                Long userId = StpUtil.getLoginIdAsLong();

                // 检查是否已点赞
                LambdaQueryWrapper<SnippetLike> likeWrapper = new LambdaQueryWrapper<>();
                likeWrapper.eq(SnippetLike::getUserId, userId)
                        .eq(SnippetLike::getSnippetId, snippet.getId());
                Long likeCount = snippetLikeMapper.selectCount(likeWrapper);
                vo.setHasLiked(likeCount > 0);

                // 检查是否已收藏
                LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
                favoriteWrapper.eq(Favorite::getUserId, userId)
                        .eq(Favorite::getSnippetId, snippet.getId());
                Long favoriteCount = favoriteMapper.selectCount(favoriteWrapper);
                vo.setHasFavorited(favoriteCount > 0);
            } catch (Exception e) {
                // 未登录
                vo.setHasLiked(false);
                vo.setHasFavorited(false);
            }
        }

        return vo;
    }
}
