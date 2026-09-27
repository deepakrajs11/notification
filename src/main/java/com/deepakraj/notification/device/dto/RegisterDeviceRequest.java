package com.deepakraj.notification.device.dto;

import com.deepakraj.notification.common.enums.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDeviceRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "deviceId is required") String deviceId,
        @NotNull(message = "platform is required") Platform platform,
        @NotBlank(message = "pushToken is required") String pushToken
) {
}
