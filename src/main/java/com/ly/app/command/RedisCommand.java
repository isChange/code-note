package com.ly.app.command;

import com.ly.app.common.cache.RedisCache;
import com.ly.app.common.constant.RedisConstant;
import com.ly.app.domain.entity.Tag;
import com.ly.app.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 刘燚
 * @version v1.0.0
 * @Description 数据预热
 * @createDate：2025/11/10 22:14
 * @email liuyia2022@163.com
 */
@Slf4j
@Component
public class RedisCommand implements CommandLineRunner {
    @Resource
    TagService tagService;
    @Override
    public void run(String... args) throws Exception {
        List<Tag> list = tagService.list();
        RedisCache.setList(RedisConstant.ALL_TAG, list);
    }
}
