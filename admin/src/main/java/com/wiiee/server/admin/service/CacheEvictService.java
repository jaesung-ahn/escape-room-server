package com.wiiee.server.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheEvictService {

    private static final String CACHE_KEY_PREFIX = "wiiee:";

    private final StringRedisTemplate redisTemplate;

    public void evictCache(String cacheName) {
        try {
            String pattern = CACHE_KEY_PREFIX + cacheName + "::*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Evicted {} cache keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache '{}': {}", cacheName, e.getMessage());
        }
    }
}
