package com.deepakraj.notification.contact.api;

import com.deepakraj.notification.contact.dto.UserContactRequest;
import com.deepakraj.notification.contact.dto.UserContactResponse;
import com.deepakraj.notification.contact.service.UserContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class UserContactController {

    private final UserContactService userContactService;

    @PostMapping
    public ResponseEntity<UserContactResponse> create(@Valid @RequestBody UserContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userContactService.create(request));
    }

    @PutMapping("/{userId}")
    public UserContactResponse update(@PathVariable String userId, @Valid @RequestBody UserContactRequest request) {
        return userContactService.update(userId, request);
    }

    @GetMapping("/{userId}")
    public UserContactResponse get(@PathVariable String userId) {
        return userContactService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivate(@PathVariable String userId) {
        userContactService.deactivate(userId);
        return ResponseEntity.noContent().build();
    }
}
