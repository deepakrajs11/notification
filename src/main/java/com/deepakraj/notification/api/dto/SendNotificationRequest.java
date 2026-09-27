package com.deepakraj.notification.api.dto;

import com.deepakraj.notification.common.enums.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendNotificationRequest(
        @NotNull(message = "channel is required") Channel channel,
        @NotBlank(message = "templateCode is required") String templateCode,
        @NotBlank(message = "userId is required") String userId,
        String recipientOverride,
        Map<String, Object> variables
) {
}
