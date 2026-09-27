package com.deepakraj.notification.device.api;

import com.deepakraj.notification.device.dto.RegisterDeviceRequest;
import com.deepakraj.notification.device.dto.UserDeviceResponse;
import com.deepakraj.notification.device.service.UserDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @PostMapping
    public ResponseEntity<UserDeviceResponse> register(@Valid @RequestBody RegisterDeviceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDeviceService.register(request));
    }

    @GetMapping
    public List<UserDeviceResponse> listByUser(@RequestParam String userId) {
        return userDeviceService.listByUser(userId);
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deactivate(@PathVariable String deviceId) {
        userDeviceService.deactivate(deviceId);
        return ResponseEntity.noContent().build();
    }
}
