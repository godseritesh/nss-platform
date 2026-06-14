package com.nssplatform.shared.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, int[]> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxRequestsPerMinute) {
        int now = (int) (Instant.now().getEpochSecond() / 60);
        var bucket = buckets.computeIfAbsent(key, k -> new int[2]);
        synchronized (bucket) {
            if (bucket[0] != now) {
                bucket[0] = now;
                bucket[1] = 0;
            }
            if (bucket[1] >= maxRequestsPerMinute) return false;
            bucket[1]++;
            return true;
        }
    }

    public void reset(String key) {
        buckets.remove(key);
    }
}
