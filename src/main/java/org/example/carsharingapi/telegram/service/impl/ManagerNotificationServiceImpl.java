package org.example.carsharingapi.telegram.service.impl;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.repository.UserRepository;
import org.example.carsharingapi.telegram.service.ManagerNotificationService;
import org.example.carsharingapi.telegram.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ManagerNotificationServiceImpl implements ManagerNotificationService {
    private static final Set<UserRole> REQUIRED_PERMISSION =
            Set.of(UserRole.CUSTOMER, UserRole.MANAGER);

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public void notifyAllManagers(String message) {
        List<User> managers = userRepository.findAllByRoles(REQUIRED_PERMISSION);

        managers.forEach(m -> {
            notificationService.sendNotification(m.getId(), message);
        });
    }
}
