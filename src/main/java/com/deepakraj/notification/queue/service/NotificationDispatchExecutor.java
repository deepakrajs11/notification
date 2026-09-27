package com.deepakraj.notification.queue.service;

import com.deepakraj.notification.common.enums.QueueStatus;
import com.deepakraj.notification.dispatch.ChannelProviderRegistry;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.dispatch.NotificationChannelProvider;
import com.deepakraj.notification.log.service.NotificationLogService;
import com.deepakraj.notification.queue.RetryPolicy;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.queue.repository.NotificationQueueRepository;
import com.deepakraj.notification.template.render.RenderedContent;
import com.deepakraj.notification.template.service.TemplateService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchExecutor {

    private final NotificationQueueRepository notificationQueueRepository;
    private final TemplateService templateService;
    private final ChannelProviderRegistry channelProviderRegistry;
    private final NotificationLogService notificationLogService;
    private final RetryPolicy retryPolicy;
    private final ObjectMapper objectMapper;

    @Transactional
    public boolean claim(Long id, QueueStatus expectedStatus) {
        return notificationQueueRepository.claim(id, expectedStatus, QueueStatus.PROCESSING) > 0;
    }

    @Transactional
    public void process(Long id) {
        NotificationQueueEntry entry = notificationQueueRepository.findById(id).orElse(null);
        if (entry == null) {
            return;
        }
        int attemptNumber = entry.getAttemptCount() + 1;
        try {
            Map<String, Object> variables = parseVariables(entry.getVariablesJson());
            RenderedContent content = templateService.render(entry.getTemplateCode(), entry.getChannel(), variables);
            NotificationChannelProvider provider = channelProviderRegistry.get(entry.getChannel());
            List<DeliveryResult> results = provider.send(entry, content);

            for (DeliveryResult result : results) {
                notificationLogService.record(entry.getTrackingId(), entry.getChannel(), entry.getTemplateCode(),
                        entry.getUserId(), attemptNumber, result);
            }

            boolean anySuccess = results.stream().anyMatch(DeliveryResult::success);
            if (anySuccess) {
                entry.setStatus(QueueStatus.SENT);
                entry.setAttemptCount(attemptNumber);
                entry.setLastError(null);
            } else {
                markFailed(entry, attemptNumber, aggregateErrors(results));
            }
        } catch (Exception ex) {
            log.warn("Delivery attempt {} failed for tracking {}", attemptNumber, entry.getTrackingId(), ex);
            notificationLogService.record(entry.getTrackingId(), entry.getChannel(), entry.getTemplateCode(),
                    entry.getUserId(), attemptNumber, DeliveryResult.failure(resolveRecipientLabel(entry), ex.getMessage()));
            markFailed(entry, attemptNumber, ex.getMessage());
        }
        notificationQueueRepository.save(entry);
    }

    private void markFailed(NotificationQueueEntry entry, int attemptNumber, String error) {
        entry.setAttemptCount(attemptNumber);
        entry.setLastError(error);
        if (attemptNumber >= entry.getMaxAttempts()) {
            entry.setStatus(QueueStatus.DEAD_LETTER);
        } else {
            entry.setStatus(QueueStatus.RETRY_SCHEDULED);
            entry.setNextAttemptAt(retryPolicy.nextAttemptAt(attemptNumber));
        }
    }

    private String resolveRecipientLabel(NotificationQueueEntry entry) {
        return entry.getRecipientOverride() != null ? entry.getRecipientOverride() : entry.getUserId();
    }

    private String aggregateErrors(List<DeliveryResult> results) {
        return results.stream()
                .map(DeliveryResult::errorMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .collect(Collectors.joining("; "));
    }

    private Map<String, Object> parseVariables(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            log.warn("Failed to parse variablesJson, falling back to empty variables", ex);
            return Map.of();
        }
    }
}
