package com.ly.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.app.common.constant.UserConstant;
import com.ly.app.common.model.DeleteRequest;
import com.ly.app.common.model.Result;
import com.ly.app.domain.dto.tag.TagCreateRequest;
import com.ly.app.domain.dto.tag.TagQueryRequest;
import com.ly.app.domain.dto.tag.TagUpdateRequest;
import com.ly.app.domain.vo.tag.TagVO;
import com.ly.app.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 * @author admin
 */
@Api(tags = "标签模块")
@Slf4j
@RequestMapping("/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @ApiOperation("创建标签")
    @PostMapping("/create")
    public Result<Long> createTag(@Validated @RequestBody TagCreateRequest createRequest) {
        Long id = tagService.createTag(createRequest);
        return Result.success(id);
    }

    @ApiOperation("更新标签")
    @PutMapping("/update")
    public Result<Boolean> updateTag(@Validated @RequestBody TagUpdateRequest updateRequest) {
        Boolean success = tagService.updateTag(updateRequest);
        return Result.success(success);
    }

    @ApiOperation("删除标签")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        Boolean success = tagService.deleteTag(deleteRequest.getId());
        return Result.success(success);
    }

    @ApiOperation("根据ID获取标签")
    @GetMapping("/get/{id}")
    public Result<TagVO> getTagById(@PathVariable("id") Long id) {
        TagVO vo = tagService.getTagById(id);
        return Result.success(vo);
    }

    @ApiOperation("分页查询标签")
    @PostMapping("/list/page")
    public Result<IPage<TagVO>> pageTags(@RequestBody TagQueryRequest queryRequest) {
        IPage<TagVO> page = tagService.pageTags(queryRequest);
        return Result.success(page);
    }

    @ApiOperation("获取所有标签")
    @GetMapping("/list/all")
    public Result<List<TagVO>> listAllTags() {
        List<TagVO> tags = tagService.listAllTags();
        return Result.success(tags);
    }

    @ApiOperation("根据代码片段ID获取标签列表")
    @GetMapping("/list/snippet/{snippetId}")
    public Result<List<TagVO>> listTagsBySnippetId(@PathVariable("snippetId") Long snippetId) {
        List<TagVO> tags = tagService.listTagsBySnippetId(snippetId);
        return Result.success(tags);
    }
}
