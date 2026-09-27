package com.deepakraj.notification.contact.dto;

import com.deepakraj.notification.contact.entity.UserContact;

import java.time.Instant;

public record UserContactResponse(
        Long id,
        String userId,
        String email,
        String displayName,
        String locale,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserContactResponse from(UserContact entity) {
        return new UserContactResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getLocale(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
