package com.deepakraj.notification.dispatch.push;

import com.deepakraj.notification.common.enums.Platform;
import com.deepakraj.notification.common.exception.NotificationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlatformSenderRegistry {

    private final Map<Platform, PlatformPushSender> senders = new HashMap<>();

    public PlatformSenderRegistry(List<PlatformPushSender> senders) {
        for (PlatformPushSender sender : senders) {
            for (Platform platform : sender.getSupportedPlatforms()) {
                this.senders.put(platform, sender);
            }
        }
    }

    public PlatformPushSender get(Platform platform) {
        PlatformPushSender sender = senders.get(platform);
        if (sender == null) {
            throw new NotificationException("No PlatformPushSender registered for platform " + platform);
        }
        return sender;
    }
}
