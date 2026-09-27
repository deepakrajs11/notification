package com.deepakraj.notification.dispatch.push;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.device.entity.UserDevice;
import com.deepakraj.notification.device.service.UserDeviceService;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.dispatch.NotificationChannelProvider;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.template.render.RenderedContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PushChannelProvider implements NotificationChannelProvider {

    private final UserDeviceService userDeviceService;
    private final PlatformSenderRegistry platformSenderRegistry;

    @Override
    public Channel getChannel() {
        return Channel.PUSH;
    }

    @Override
    public List<DeliveryResult> send(NotificationQueueEntry entry, RenderedContent content) {
        List<UserDevice> devices = userDeviceService.findActiveDevices(entry.getUserId());
        if (devices.isEmpty()) {
            return List.of(DeliveryResult.failure(entry.getUserId(), "No active devices registered for userId " + entry.getUserId()));
        }
        return devices.stream()
                .map(device -> platformSenderRegistry.get(device.getPlatform()).send(device, content))
                .toList();
    }
}
