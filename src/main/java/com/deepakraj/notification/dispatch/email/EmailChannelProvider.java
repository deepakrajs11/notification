package com.deepakraj.notification.dispatch.email;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.exception.RecipientNotFoundException;
import com.deepakraj.notification.config.MailProperties;
import com.deepakraj.notification.contact.entity.UserContact;
import com.deepakraj.notification.contact.service.UserContactService;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.dispatch.NotificationChannelProvider;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.template.render.RenderedContent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailChannelProvider implements NotificationChannelProvider {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private final UserContactService userContactService;

    @Override
    public Channel getChannel() {
        return Channel.EMAIL;
    }

    @Override
    public List<DeliveryResult> send(NotificationQueueEntry entry, RenderedContent content) {
        String recipientEmail = resolveRecipient(entry);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setFrom(mailProperties.getFrom());
            helper.setSubject(content.subjectOrTitle());
            helper.setText(content.body(), true);
            javaMailSender.send(message);
            return List.of(DeliveryResult.success(recipientEmail, UUID.randomUUID().toString()));
        } catch (MailException | jakarta.mail.MessagingException ex) {
            return List.of(DeliveryResult.failure(recipientEmail, ex.getMessage()));
        }
    }

    private String resolveRecipient(NotificationQueueEntry entry) {
        if (entry.getRecipientOverride() != null && !entry.getRecipientOverride().isBlank()) {
            return entry.getRecipientOverride();
        }
        UserContact contact = userContactService.findActiveByUserId(entry.getUserId())
                .orElseThrow(() -> new RecipientNotFoundException(
                        "No active email contact found for userId " + entry.getUserId()));
        return contact.getEmail();
    }
}
