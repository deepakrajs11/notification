package com.deepakraj.notification.dispatch;

public record DeliveryResult(boolean success, String recipient, String providerMessageId, String errorMessage) {

    public static DeliveryResult success(String recipient, String providerMessageId) {
        return new DeliveryResult(true, recipient, providerMessageId, null);
    }

    public static DeliveryResult failure(String recipient, String errorMessage) {
        return new DeliveryResult(false, recipient, null, errorMessage);
    }
}
