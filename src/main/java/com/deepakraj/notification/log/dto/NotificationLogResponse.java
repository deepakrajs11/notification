package com.deepakraj.notification.log.dto;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.enums.LogStatus;
import com.deepakraj.notification.log.entity.NotificationLog;

import java.time.Instant;

public record NotificationLogResponse(
        Long id,
        String trackingId,
        Channel channel,
        String templateCode,
        String userId,
        String recipient,
        int attemptNumber,
        LogStatus status,
        String providerMessageId,
        String errorMessage,
        Instant createdAt
) {
    public static NotificationLogResponse from(NotificationLog entity) {
        return new NotificationLogResponse(
                entity.getId(),
                entity.getTrackingId(),
                entity.getChannel(),
                entity.getTemplateCode(),
                entity.getUserId(),
                entity.getRecipient(),
                entity.getAttemptNumber(),
                entity.getStatus(),
                entity.getProviderMessageId(),
                entity.getErrorMessage(),
                entity.getCreatedAt()
        );
    }
}
