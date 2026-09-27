package com.deepakraj.notification.config;

import com.deepakraj.notification.dispatch.push.PushProperties;
import com.deepakraj.notification.queue.QueueProperties;
import com.deepakraj.notification.queue.RetryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationPropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "notification.queue")
    public QueueProperties queueProperties() {
        return new QueueProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "notification.retry")
    public RetryProperties retryProperties() {
        return new RetryProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "notification.push")
    public PushProperties pushProperties() {
        return new PushProperties();
    }
}
