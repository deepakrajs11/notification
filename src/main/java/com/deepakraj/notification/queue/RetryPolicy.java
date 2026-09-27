package com.deepakraj.notification.queue;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class RetryPolicy {

    private final RetryProperties retryProperties;

    public RetryPolicy(RetryProperties retryProperties) {
        this.retryProperties = retryProperties;
    }

    /**
     * attemptCount is the number of attempts already made (1 after the first failure).
     */
    public Instant nextAttemptAt(int attemptCount) {
        return Instant.now().plus(nextDelay(attemptCount));
    }

    public Duration nextDelay(int attemptCount) {
        double raw = retryProperties.getBaseDelayMs() * Math.pow(retryProperties.getMultiplier(), Math.max(0, attemptCount - 1));
        long cappedMs = (long) Math.min(raw, retryProperties.getMaxDelayMs());
        return Duration.ofMillis(cappedMs);
    }
}
