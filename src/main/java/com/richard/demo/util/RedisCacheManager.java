package com.richard.demo.util;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCacheManager {
    private final CacheManager cacheManager;
    private final RedisTemplate redisTemplate;

    public Collection<String> getAllCacheKeys(String cacheName) {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return cacheKeys;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    public void clearCache(String cacheName, String key) {
        Cache cache = this.getCache(cacheName);
        if (null != cache) {
            cache.evict(key);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearCache(String cacheName, String cacheMethodName, String... params) {
        this.clearCache(cacheName, this.getCacheKey(cacheMethodName, params));
    }

    public String getCacheKey(String cacheMethodName, String... params) {
        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(cacheMethodName);
        if (params.length > 0) {
            for (Object param : params) {
                cacheKey.append("_").append(param);
            }
        }
        log.info("[RedisCacheManager#getCacheKey] Cache key: {}.", cacheKey.toString());
        return cacheKey.toString();
    }

    public void clearAllCacheValues(String cacheName) {
        Cache cache = this.getCache(cacheName);
        if (null != cache) {
            cache.clear();
        }
    }

    public <T> T get(String cacheName, Class<T> type, String cacheMethodName, String... params) {
        return this.get(cacheName, type, getCacheKey(cacheMethodName, params));
    }

    public <T> T get(String cacheName, Class<T> type, String key) {
        T value = null;
        Cache cache = this.getCache(cacheName);
        if (null != cache) {
            value = cache.get(key, type);
        }
        return value;
    }

    public void put(String cacheName, String key, @Nullable Object value) {
        Cache cache = getCache(cacheName);
        if (null != cache) {
            cache.put(key, value);
        }
    }

    public Cache getCache(String cacheName) {
        Cache cache = null;
        if (null != cacheName) {
            cache = cacheManager.getCache(cacheName);
        }
        return cache;
    }
}
