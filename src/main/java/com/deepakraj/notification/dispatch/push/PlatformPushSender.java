package com.deepakraj.notification.dispatch.push;

import com.deepakraj.notification.common.enums.Platform;
import com.deepakraj.notification.device.entity.UserDevice;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.template.render.RenderedContent;

import java.util.Set;

public interface PlatformPushSender {

    Set<Platform> getSupportedPlatforms();

    DeliveryResult send(UserDevice device, RenderedContent content);
}
