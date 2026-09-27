package com.deepakraj.notification.config;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.template.entity.NotificationTemplate;
import com.deepakraj.notification.template.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TemplateSeeder implements CommandLineRunner {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seed("welcome_email", Channel.EMAIL, "Welcome email sent right after signup");
        seed("welcome_push", Channel.PUSH, "Welcome push notification sent right after signup");
    }

    private void seed(String code, Channel channel, String description) {
        if (notificationTemplateRepository.existsByCode(code)) {
            return;
        }
        NotificationTemplate template = new NotificationTemplate();
        template.setCode(code);
        template.setChannel(channel);
        template.setDescription(description);
        template.setActive(true);
        notificationTemplateRepository.save(template);
    }
}
