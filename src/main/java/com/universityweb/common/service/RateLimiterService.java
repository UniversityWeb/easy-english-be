package com.universityweb.common.service;

public interface RateLimiterService {
    /**
     * Checks if a request is allowed based on the rate limit.
     *
     * @param key               The unique key to track
     * @param maxRequests       The maximum number of requests allowed within the time window
     * @param timeWindowSeconds The duration of the time window in seconds
     * @return true if the request is allowed, false if the limit is exceeded
     */
    boolean isAllowed(String key, int maxRequests, long timeWindowSeconds);
}
