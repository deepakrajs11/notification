package com.deepakraj.notification.log.service;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.enums.LogStatus;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.log.dto.NotificationLogResponse;
import com.deepakraj.notification.log.entity.NotificationLog;
import com.deepakraj.notification.log.repository.NotificationLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationLogServiceImpl implements NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    @Override
    public void record(String trackingId, Channel channel, String templateCode, String userId, int attemptNumber, DeliveryResult result) {
        NotificationLog log = new NotificationLog();
        log.setTrackingId(trackingId);
        log.setChannel(channel);
        log.setTemplateCode(templateCode);
        log.setUserId(userId);
        log.setRecipient(result.recipient());
        log.setAttemptNumber(attemptNumber);
        log.setStatus(result.success() ? LogStatus.SENT : LogStatus.FAILED);
        log.setProviderMessageId(result.providerMessageId());
        log.setErrorMessage(result.errorMessage());
        notificationLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationLogResponse> findByTrackingId(String trackingId) {
        return notificationLogRepository.findByTrackingIdOrderByCreatedAtAsc(trackingId).stream()
                .map(NotificationLogResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationLogResponse> search(String userId, Channel channel, LogStatus status, Pageable pageable) {
        Specification<NotificationLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null && !userId.isBlank()) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (channel != null) {
                predicates.add(cb.equal(root.get("channel"), channel));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return notificationLogRepository.findAll(spec, pageable).map(NotificationLogResponse::from);
    }
}
