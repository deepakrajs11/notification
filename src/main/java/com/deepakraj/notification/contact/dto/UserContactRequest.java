package com.deepakraj.notification.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserContactRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,
        String displayName,
        String locale
) {
}
