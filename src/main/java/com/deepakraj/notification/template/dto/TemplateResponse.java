package com.deepakraj.notification.template.dto;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.template.entity.NotificationTemplate;

import java.time.Instant;

public record TemplateResponse(
        Long id,
        String code,
        Channel channel,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static TemplateResponse from(NotificationTemplate entity) {
        return new TemplateResponse(
                entity.getId(),
                entity.getCode(),
                entity.getChannel(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
