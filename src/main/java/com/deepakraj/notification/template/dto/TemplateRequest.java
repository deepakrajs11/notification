package com.deepakraj.notification.template.dto;

import com.deepakraj.notification.common.enums.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TemplateRequest(
        @NotBlank(message = "code is required") String code,
        @NotNull(message = "channel is required") Channel channel,
        String description
) {
}
