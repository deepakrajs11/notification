package com.deepakraj.notification.queue;

import lombok.Data;

@Data
public class RetryProperties {

    private long baseDelayMs = 30_000;
    private double multiplier = 2.0;
    private long maxDelayMs = 3_600_000;
}
