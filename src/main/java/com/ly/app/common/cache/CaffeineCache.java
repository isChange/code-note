package com.ly.app.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.ly.app.common.units.SpringContextUtil;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Description: CaffeineCache工具类
 *
 * @author: yi.liu
 */
public class CaffeineCache {

    private static Cache<String, Object> cache = (Cache<String, Object>) SpringContextUtil.getBean(Cache.class);


    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public static Object get(Object key) {
        return cache.getIfPresent(key);
    }

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void set(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 设置缓存时间
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     * @param unit     单位
     */
    public static void set(String key, Object value, long duration, TimeUnit unit) {
        cache.policy().expireVariably().ifPresent(e -> e.put(key, value, duration, unit));
    }

    /**
     * 移除缓存项
     *
     * @param key 缓存键
     */
    public static void remove(Object key) {
        cache.invalidate(key);
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        cache.invalidateAll();
    }

    /**
     * 获取缓存中的所有值
     *
     * @return 缓存中的所有值集合
     */
    public static Collection<Object> getAllValues() {
        return cache.asMap().values();
    }

    /**
     * 清空缓存中的所有值
     */
    public static void removeAllValues() {
        cache.invalidateAll();
    }
}
