package com.deepakraj.notification.log.service;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.enums.LogStatus;
import com.deepakraj.notification.dispatch.DeliveryResult;
import com.deepakraj.notification.log.dto.NotificationLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationLogService {

    void record(String trackingId, Channel channel, String templateCode, String userId, int attemptNumber, DeliveryResult result);

    List<NotificationLogResponse> findByTrackingId(String trackingId);

    Page<NotificationLogResponse> search(String userId, Channel channel, LogStatus status, Pageable pageable);
}
