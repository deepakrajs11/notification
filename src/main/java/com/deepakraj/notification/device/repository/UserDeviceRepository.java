package com.deepakraj.notification.device.repository;

import com.deepakraj.notification.common.enums.DeviceStatus;
import com.deepakraj.notification.device.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByDeviceId(String deviceId);

    List<UserDevice> findByUserIdAndStatus(String userId, DeviceStatus status);

    List<UserDevice> findByUserId(String userId);
}
