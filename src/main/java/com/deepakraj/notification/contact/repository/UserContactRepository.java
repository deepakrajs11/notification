package com.deepakraj.notification.contact.repository;

import com.deepakraj.notification.contact.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {

    Optional<UserContact> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
