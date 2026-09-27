package com.deepakraj.notification.log.repository;

import com.deepakraj.notification.log.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long>, JpaSpecificationExecutor<NotificationLog> {

    List<NotificationLog> findByTrackingIdOrderByCreatedAtAsc(String trackingId);
}
