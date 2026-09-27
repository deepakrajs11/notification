package com.deepakraj.notification.device.service;

import com.deepakraj.notification.device.dto.RegisterDeviceRequest;
import com.deepakraj.notification.device.dto.UserDeviceResponse;
import com.deepakraj.notification.device.entity.UserDevice;

import java.util.List;

public interface UserDeviceService {

    UserDeviceResponse register(RegisterDeviceRequest request);

    void deactivate(String deviceId);

    List<UserDeviceResponse> listByUser(String userId);

    List<UserDevice> findActiveDevices(String userId);
}
