package com.ly.app.common.config;

import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @ClassName CaffeineExpiry
 * @author: yi.liu
 */
public class CaffeineExpiry implements Expiry<String, Object> {

    //创建后的持续时间过了，就自动从缓存中删除
    @Override
    public long expireAfterCreate(@NonNull String key, @NonNull Object value, long currentTime) {
        return getMaxInterval(key);
    }

    //在更新后的持续时间一过，就应自动从缓存中删除该条目。
    @Override
    public long expireAfterUpdate(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return getMaxInterval(key);
    }

    //超过读取后的持续时间，就自动从缓存中删除
    @Override
    public long expireAfterRead(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return currentDuration;
    }

    /**
     * 返回纳秒数，用不过期兜底
     */
    private long getMaxInterval(Object key) {
        String[] keyStr = key.toString().split("#");
        return keyStr.length > 1 ? TimeUnit.SECONDS.toNanos(Long.valueOf(keyStr[1])) : TimeUnit.DAYS.toNanos(Long.MAX_VALUE);
    }

}
