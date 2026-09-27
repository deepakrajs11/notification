package com.deepakraj.notification.dispatch.push;

import com.deepakraj.notification.common.enums.Platform;
import com.deepakraj.notification.device.entity.UserDevice;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.template.render.RenderedContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

/**
 * Stand-in for the real Firebase Admin SDK client - swap in an "fcm.enabled=true" bean of the
 * same interface later without touching PushChannelProvider or the worker.
 */
@Component
@ConditionalOnProperty(prefix = "notification.push.fcm", name = "enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class FcmNoOpPushSender implements PlatformPushSender {

    @Override
    public Set<Platform> getSupportedPlatforms() {
        return Set.of(Platform.ANDROID, Platform.WEB);
    }

    @Override
    public DeliveryResult send(UserDevice device, RenderedContent content) {
        log.info("[FCM-NOOP] would push to deviceId={} platform={} title='{}'",
                device.getDeviceId(), device.getPlatform(), content.subjectOrTitle());
        return DeliveryResult.success(device.getDeviceId(), "fcm-noop-" + UUID.randomUUID());
    }
}
