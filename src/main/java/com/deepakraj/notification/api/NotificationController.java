package com.deepakraj.notification.api;

import com.deepakraj.notification.api.dto.NotificationAcceptedResponse;
import com.deepakraj.notification.api.dto.NotificationStatusResponse;
import com.deepakraj.notification.api.dto.SendNotificationRequest;
import com.deepakraj.notification.queue.service.NotificationQueryService;
import com.deepakraj.notification.queue.service.NotificationQueuePublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueuePublisher notificationQueuePublisher;
    private final NotificationQueryService notificationQueryService;

    @PostMapping
    public ResponseEntity<NotificationAcceptedResponse> send(@Valid @RequestBody SendNotificationRequest request) {
        String trackingId = notificationQueuePublisher.enqueue(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new NotificationAcceptedResponse(trackingId, "QUEUED"));
    }

    @GetMapping("/{trackingId}")
    public NotificationStatusResponse status(@PathVariable String trackingId) {
        return notificationQueryService.getStatus(trackingId);
    }
}
