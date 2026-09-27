package com.deepakraj.notification.contact.service;

import com.deepakraj.notification.contact.dto.UserContactRequest;
import com.deepakraj.notification.contact.dto.UserContactResponse;
import com.deepakraj.notification.contact.entity.UserContact;
import com.deepakraj.notification.contact.repository.UserContactRepository;
import com.deepakraj.notification.common.exception.DuplicateResourceException;
import com.deepakraj.notification.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional("contactTransactionManager")
public class UserContactServiceImpl implements UserContactService {

    private final UserContactRepository userContactRepository;

    @Override
    public UserContactResponse create(UserContactRequest request) {
        if (userContactRepository.existsByUserId(request.userId())) {
            throw new DuplicateResourceException("A contact already exists for userId " + request.userId());
        }
        UserContact contact = new UserContact();
        contact.setUserId(request.userId());
        applyRequest(contact, request);
        return UserContactResponse.from(userContactRepository.save(contact));
    }

    @Override
    public UserContactResponse update(String userId, UserContactRequest request) {
        UserContact contact = requireByUserId(userId);
        applyRequest(contact, request);
        return UserContactResponse.from(userContactRepository.save(contact));
    }

    @Override
    @Transactional(value = "contactTransactionManager", readOnly = true)
    public UserContactResponse get(String userId) {
        return UserContactResponse.from(requireByUserId(userId));
    }

    @Override
    public void deactivate(String userId) {
        UserContact contact = requireByUserId(userId);
        contact.setActive(false);
        userContactRepository.save(contact);
    }

    @Override
    @Transactional(value = "contactTransactionManager", readOnly = true)
    public Optional<UserContact> findActiveByUserId(String userId) {
        return userContactRepository.findByUserId(userId).filter(UserContact::isActive);
    }

    private void applyRequest(UserContact contact, UserContactRequest request) {
        contact.setEmail(request.email());
        contact.setDisplayName(request.displayName());
        contact.setLocale(request.locale() == null || request.locale().isBlank() ? "en" : request.locale());
        contact.setActive(true);
    }

    private UserContact requireByUserId(String userId) {
        return userContactRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No contact found for userId " + userId));
    }
}
