package com.nssplatform.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RateLimiterTest {

    private final RateLimiter limiter = new RateLimiter();

    @Test
    void allowsUpToMaxRequests() {
        String key = "test:user@example.com";
        for (int i = 0; i < 5; i++) {
            assertThat(limiter.isAllowed(key, 5)).isTrue();
        }
        assertThat(limiter.isAllowed(key, 5)).isFalse();
    }

    @Test
    void differentKeysHaveSeparateBuckets() {
        assertThat(limiter.isAllowed("key1", 1)).isTrue();
        assertThat(limiter.isAllowed("key2", 1)).isTrue();
        assertThat(limiter.isAllowed("key1", 1)).isFalse();
        assertThat(limiter.isAllowed("key2", 1)).isFalse();
    }

    @Test
    void resetClearsBucket() {
        String key = "reset:test";
        limiter.isAllowed(key, 1);
        limiter.isAllowed(key, 1);
        limiter.reset(key);
        assertThat(limiter.isAllowed(key, 1)).isTrue();
    }

    @Test
    void allowsZeroMaxRequests() {
        assertThat(limiter.isAllowed("zero:test", 0)).isFalse();
    }
}
