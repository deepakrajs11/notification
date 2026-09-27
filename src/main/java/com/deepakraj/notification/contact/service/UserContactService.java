package com.deepakraj.notification.contact.service;

import com.deepakraj.notification.contact.dto.UserContactRequest;
import com.deepakraj.notification.contact.dto.UserContactResponse;
import com.deepakraj.notification.contact.entity.UserContact;

import java.util.Optional;

public interface UserContactService {

    UserContactResponse create(UserContactRequest request);

    UserContactResponse update(String userId, UserContactRequest request);

    UserContactResponse get(String userId);

    void deactivate(String userId);

    Optional<UserContact> findActiveByUserId(String userId);
}
