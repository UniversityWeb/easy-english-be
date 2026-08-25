package com.universityweb.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RateLimiterServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isAllowed(String key, int maxRequests, long timeWindowSeconds) {
        long currentWindow = System.currentTimeMillis() / 1000 / timeWindowSeconds;
        String redisKey = key + ":" + currentWindow;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1) {
            // Set expiration to slightly longer than the time window to ensure it cleans up
            redisTemplate.expire(redisKey, timeWindowSeconds + 10, TimeUnit.SECONDS);
        }

        return count != null && count <= maxRequests;
    }
}
