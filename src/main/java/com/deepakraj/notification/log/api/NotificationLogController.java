package com.deepakraj.notification.log.api;

import com.deepakraj.notification.common.enums.Channel;
import com.deepakraj.notification.common.enums.LogStatus;
import com.deepakraj.notification.log.dto.NotificationLogResponse;
import com.deepakraj.notification.log.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/logs")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogService notificationLogService;

    @GetMapping
    public Page<NotificationLogResponse> search(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Channel channel,
            @RequestParam(required = false) LogStatus status,
            Pageable pageable) {
        return notificationLogService.search(userId, channel, status, pageable);
    }
}
