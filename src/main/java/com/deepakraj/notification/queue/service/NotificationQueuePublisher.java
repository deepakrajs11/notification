package com.deepakraj.notification.queue.service;

import com.deepakraj.notification.api.dto.SendNotificationRequest;
import com.deepakraj.notification.common.enums.QueueStatus;
import com.deepakraj.notification.common.exception.NotificationException;
import com.deepakraj.notification.queue.QueueProperties;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.queue.repository.NotificationQueueRepository;
import com.deepakraj.notification.template.service.TemplateService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationQueuePublisher {

    private final NotificationQueueRepository notificationQueueRepository;
    private final TemplateService templateService;
    private final QueueProperties queueProperties;
    private final ObjectMapper objectMapper;

    public String enqueue(SendNotificationRequest request) {
        templateService.requireActiveTemplate(request.templateCode(), request.channel());

        NotificationQueueEntry entry = new NotificationQueueEntry();
        entry.setTrackingId(UUID.randomUUID().toString());
        entry.setChannel(request.channel());
        entry.setTemplateCode(request.templateCode());
        entry.setUserId(request.userId());
        entry.setRecipientOverride(request.recipientOverride());
        entry.setVariablesJson(toJson(request.variables()));
        entry.setStatus(QueueStatus.NEW);
        entry.setAttemptCount(0);
        entry.setMaxAttempts(queueProperties.getDefaultMaxAttempts());
        entry.setNextAttemptAt(Instant.now());

        notificationQueueRepository.save(entry);
        return entry.getTrackingId();
    }

    private String toJson(Map<String, Object> variables) {
        try {
            return objectMapper.writeValueAsString(variables == null ? Map.of() : variables);
        } catch (JacksonException ex) {
            throw new NotificationException("Failed to serialize notification variables", ex);
        }
    }
}
