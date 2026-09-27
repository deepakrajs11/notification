package com.deepakraj.notification.device.dto;

import com.deepakraj.notification.common.enums.DeviceStatus;
import com.deepakraj.notification.common.enums.Platform;
import com.deepakraj.notification.device.entity.UserDevice;

import java.time.Instant;

public record UserDeviceResponse(
        Long id,
        String userId,
        String deviceId,
        Platform platform,
        DeviceStatus status,
        Instant lastSeenAt,
        Instant createdAt
) {
    public static UserDeviceResponse from(UserDevice entity) {
        return new UserDeviceResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDeviceId(),
                entity.getPlatform(),
                entity.getStatus(),
                entity.getLastSeenAt(),
                entity.getCreatedAt()
        );
    }
}
