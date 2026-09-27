package com.deepakraj.notification.queue.service;

import com.deepakraj.notification.api.dto.NotificationStatusResponse;
import com.deepakraj.notification.common.exception.ResourceNotFoundException;
import com.deepakraj.notification.log.service.NotificationLogService;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.queue.repository.NotificationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationQueueRepository notificationQueueRepository;
    private final NotificationLogService notificationLogService;

    public NotificationStatusResponse getStatus(String trackingId) {
        NotificationQueueEntry entry = notificationQueueRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("No notification found for trackingId " + trackingId));

        return new NotificationStatusResponse(
                entry.getTrackingId(),
                entry.getChannel(),
                entry.getTemplateCode(),
                entry.getUserId(),
                entry.getStatus(),
                entry.getAttemptCount(),
                entry.getMaxAttempts(),
                entry.getNextAttemptAt(),
                entry.getLastError(),
                notificationLogService.findByTrackingId(trackingId)
        );
    }
}
