package com.deepakraj.notification.queue.repository;

import com.deepakraj.notification.common.enums.QueueStatus;
import com.deepakraj.notification.queue.entity.NotificationQueueEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationQueueRepository extends JpaRepository<NotificationQueueEntry, Long> {

    Optional<NotificationQueueEntry> findByTrackingId(String trackingId);

    @Query("select e from NotificationQueueEntry e where e.status in (:statuses) and e.nextAttemptAt <= :now order by e.createdAt asc")
    List<NotificationQueueEntry> findDue(@Param("statuses") List<QueueStatus> statuses, @Param("now") Instant now, Pageable pageable);

    @Modifying
    @Query("update NotificationQueueEntry e set e.status = :newStatus where e.id = :id and e.status = :expectedStatus")
    int claim(@Param("id") Long id, @Param("expectedStatus") QueueStatus expectedStatus, @Param("newStatus") QueueStatus newStatus);
}
