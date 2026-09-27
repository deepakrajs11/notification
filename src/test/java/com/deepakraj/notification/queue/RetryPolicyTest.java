package com.deepakraj.notification.queue;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RetryPolicyTest {

    private RetryPolicy newPolicy(long baseDelayMs, double multiplier, long maxDelayMs) {
        RetryProperties properties = new RetryProperties();
        properties.setBaseDelayMs(baseDelayMs);
        properties.setMultiplier(multiplier);
        properties.setMaxDelayMs(maxDelayMs);
        return new RetryPolicy(properties);
    }

    @Test
    void firstAttemptUsesBaseDelay() {
        RetryPolicy policy = newPolicy(1000, 2.0, 1_000_000);
        assertThat(policy.nextDelay(1)).isEqualTo(Duration.ofMillis(1000));
    }

    @Test
    void delayGrowsExponentiallyWithAttemptCount() {
        RetryPolicy policy = newPolicy(1000, 2.0, 1_000_000);
        assertThat(policy.nextDelay(2)).isEqualTo(Duration.ofMillis(2000));
        assertThat(policy.nextDelay(3)).isEqualTo(Duration.ofMillis(4000));
        assertThat(policy.nextDelay(4)).isEqualTo(Duration.ofMillis(8000));
    }

    @Test
    void delayIsCappedAtMaxDelay() {
        RetryPolicy policy = newPolicy(1000, 2.0, 5000);
        assertThat(policy.nextDelay(10)).isEqualTo(Duration.ofMillis(5000));
    }

    @Test
    void nextAttemptAtIsInTheFuture() {
        RetryPolicy policy = newPolicy(1000, 2.0, 1_000_000);
        assertThat(policy.nextAttemptAt(1)).isAfter(java.time.Instant.now());
    }
}
