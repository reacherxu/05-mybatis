package com.richard.demo.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisCacheManager {
    private final CacheManager cacheManager;

    public RedisCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    public Map<String, Object> ObjToMap(Object obj) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

    public void clearCache(String cacheName, String key) {
        Cache cache = this.getCache(cacheName);
        if (null != cache) {
            cache.evict(key);
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
