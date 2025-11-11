package com.ly.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.app.common.model.DeleteRequest;
import com.ly.app.common.model.Result;
import com.ly.app.domain.dto.snippet.SnippetCreateRequest;
import com.ly.app.domain.dto.snippet.SnippetQueryRequest;
import com.ly.app.domain.dto.snippet.SnippetUpdateRequest;
import com.ly.app.domain.vo.snippet.CodeSnippetVO;
import com.ly.app.service.CodeSnippetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代码片段控制器
 * @author admin
 */
@Api(tags = "代码片段模块")
@Slf4j
@RequestMapping("/snippet")
@RestController
@RequiredArgsConstructor
public class CodeSnippetController {

    private final CodeSnippetService codeSnippetService;

    @ApiOperation("创建代码片段")
    @PostMapping("/create")
    public Result<Long> createSnippet(@Validated @RequestBody SnippetCreateRequest createRequest) {
        Long id = codeSnippetService.createSnippet(createRequest);
        return Result.success(id);
    }

    @ApiOperation("更新代码片段")
    @PutMapping("/update")
    public Result<Boolean> updateSnippet(@Validated @RequestBody SnippetUpdateRequest updateRequest) {
        Boolean success = codeSnippetService.updateSnippet(updateRequest);
        return Result.success(success);
    }

    @ApiOperation("删除代码片段")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteSnippet(@RequestBody DeleteRequest deleteRequest) {
        Boolean success = codeSnippetService.deleteSnippet(deleteRequest.getId());
        return Result.success(success);
    }

    @ApiOperation("根据ID获取代码片段")
    @GetMapping("/get/{id}")
    public Result<CodeSnippetVO> getSnippetById(@PathVariable("id") Long id) {
        // 增加浏览次数
        codeSnippetService.incrementViewCount(id);
        CodeSnippetVO vo = codeSnippetService.getSnippetById(id);
        return Result.success(vo);
    }

    @ApiOperation("分页查询代码片段")
    @PostMapping("/list/page")
    public Result<IPage<CodeSnippetVO>> pageSnippets(@RequestBody SnippetQueryRequest queryRequest) {
        IPage<CodeSnippetVO> page = codeSnippetService.pageSnippets(queryRequest);
        return Result.success(page);
    }

    @ApiOperation("点赞代码片段")
    @PostMapping("/like/{id}")
    public Result<Boolean> likeSnippet(@PathVariable("id") Long snippetId) {
        Boolean success = codeSnippetService.likeSnippet(snippetId);
        return Result.success(success);
    }

    @ApiOperation("取消点赞代码片段")
    @PostMapping("/unlike/{id}")
    public Result<Boolean> unlikeSnippet(@PathVariable("id") Long snippetId) {
        Boolean success = codeSnippetService.unlikeSnippet(snippetId);
        return Result.success(success);
    }

    @ApiOperation("收藏代码片段")
    @PostMapping("/favorite/{id}")
    public Result<Boolean> favoriteSnippet(@PathVariable("id") Long snippetId) {
        Boolean success = codeSnippetService.favoriteSnippet(snippetId);
        return Result.success(success);
    }

    @ApiOperation("取消收藏代码片段")
    @PostMapping("/unfavorite/{id}")
    public Result<Boolean> unfavoriteSnippet(@PathVariable("id") Long snippetId) {
        Boolean success = codeSnippetService.unfavoriteSnippet(snippetId);
        return Result.success(success);
    }

    @ApiOperation("获取我的代码片段列表")
    @PostMapping("/list/my")
    public Result<IPage<CodeSnippetVO>> listMySnippets(@RequestBody SnippetQueryRequest queryRequest) {
        // 获取当前用户ID并设置到查询条件
        IPage<CodeSnippetVO> page = codeSnippetService.pageSnippets(queryRequest);
        return Result.success(page);
    }
}
