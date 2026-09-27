package com.deepakraj.notification.dispatch;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.exception.NotificationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ChannelProviderRegistry {

    private final Map<Channel, NotificationChannelProvider> providers;

    public ChannelProviderRegistry(List<NotificationChannelProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(NotificationChannelProvider::getChannel, Function.identity()));
    }

    public NotificationChannelProvider get(Channel channel) {
        NotificationChannelProvider provider = providers.get(channel);
        if (provider == null) {
            throw new NotificationException("No NotificationChannelProvider registered for channel " + channel);
        }
        return provider;
    }
}
