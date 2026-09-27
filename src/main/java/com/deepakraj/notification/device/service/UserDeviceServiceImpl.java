package com.deepakraj.notification.device.service;

import com.deepakraj.notification.common.enums.DeviceStatus;
import com.deepakraj.notification.common.exception.ResourceNotFoundException;
import com.deepakraj.notification.device.dto.RegisterDeviceRequest;
import com.deepakraj.notification.device.dto.UserDeviceResponse;
import com.deepakraj.notification.device.entity.UserDevice;
import com.deepakraj.notification.device.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional("deviceTransactionManager")
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    @Override
    public UserDeviceResponse register(RegisterDeviceRequest request) {
        UserDevice device = userDeviceRepository.findByDeviceId(request.deviceId()).orElseGet(UserDevice::new);
        device.setUserId(request.userId());
        device.setDeviceId(request.deviceId());
        device.setPlatform(request.platform());
        device.setPushToken(request.pushToken());
        device.setStatus(DeviceStatus.ACTIVE);
        device.setLastSeenAt(Instant.now());
        return UserDeviceResponse.from(userDeviceRepository.save(device));
    }

    @Override
    public void deactivate(String deviceId) {
        UserDevice device = userDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("No device found for deviceId " + deviceId));
        device.setStatus(DeviceStatus.INACTIVE);
        userDeviceRepository.save(device);
    }

    @Override
    @Transactional(value = "deviceTransactionManager", readOnly = true)
    public List<UserDeviceResponse> listByUser(String userId) {
        return userDeviceRepository.findByUserId(userId).stream()
                .map(UserDeviceResponse::from)
                .toList();
    }

    @Override
    @Transactional(value = "deviceTransactionManager", readOnly = true)
    public List<UserDevice> findActiveDevices(String userId) {
        return userDeviceRepository.findByUserIdAndStatus(userId, DeviceStatus.ACTIVE);
    }
}
