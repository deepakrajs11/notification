package com.deepakraj.notification.queue;

import lombok.Data;

@Data
public class QueueProperties {

    private long pollIntervalMs = 5_000;
    private int batchSize = 20;
    private int defaultMaxAttempts = 5;
}
