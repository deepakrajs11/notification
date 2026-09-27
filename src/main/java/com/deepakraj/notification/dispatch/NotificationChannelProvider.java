package com.deepakraj.notification.dispatch;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.template.render.RenderedContent;

import java.util.List;

public interface NotificationChannelProvider {

    Channel getChannel();

    List<DeliveryResult> send(NotificationQueueEntry entry, RenderedContent content);
}
