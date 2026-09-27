package com.deepakraj.notification.template.repository;

import com.deepakraj.notification.template.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByCode(String code);

    boolean existsByCode(String code);
}
