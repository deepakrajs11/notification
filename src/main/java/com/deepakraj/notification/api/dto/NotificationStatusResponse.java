package com.deepakraj.notification.api.dto;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.enums.QueueStatus;
import com.deepakraj.notification.log.dto.NotificationLogResponse;

import java.time.Instant;
import java.util.List;

public record NotificationStatusResponse(
        String trackingId,
        Channel channel,
        String templateCode,
        String userId,
        QueueStatus status,
        int attemptCount,
        int maxAttempts,
        Instant nextAttemptAt,
        String lastError,
        List<NotificationLogResponse> history
) {
}
