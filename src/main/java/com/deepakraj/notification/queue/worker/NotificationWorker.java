package com.deepakraj.notification.queue.worker;

import com.deepakraj.notification.common.enums.QueueStatus;
import com.deepakraj.notification.queue.QueueProperties;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import com.deepakraj.notification.queue.repository.NotificationQueueRepository;
import com.deepakraj.notification.queue.service.NotificationDispatchExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWorker {

    private final NotificationQueueRepository notificationQueueRepository;
    private final NotificationDispatchExecutor dispatchExecutor;
    private final QueueProperties queueProperties;

    @Scheduled(fixedDelayString = "${notification.queue.poll-interval-ms:5000}")
    public void pollAndDispatch() {
        List<NotificationQueueEntry> due = notificationQueueRepository.findDue(
                List.of(QueueStatus.NEW, QueueStatus.RETRY_SCHEDULED),
                Instant.now(),
                PageRequest.of(0, queueProperties.getBatchSize()));

        for (NotificationQueueEntry entry : due) {
            try {
                if (dispatchExecutor.claim(entry.getId(), entry.getStatus())) {
                    dispatchExecutor.process(entry.getId());
                }
            } catch (Exception ex) {
                log.error("Unexpected error dispatching notification queue entry {}", entry.getId(), ex);
            }
        }
    }
}
