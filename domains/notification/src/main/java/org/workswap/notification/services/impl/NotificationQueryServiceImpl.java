package org.workswap.notification.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.notification.datasource.repository.NotificationRepository;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationMappingService;
import org.workswap.notification.services.NotificationQueryService;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationQueryService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMappingService notificationMappingService;
    
    public List<FullNotificationDTO> getUserNotifications(UserAuthData authData) {
        logger.debug("Отправляем уведомления пользователю с Id: {}", authData.id());

        List<FullNotificationDTO> notifications = notificationRepository.findByRecipientId(authData.id()).stream()
            .map(notification -> notificationMappingService.toDTO(notification))
            .collect(Collectors.toList());

        return notifications;
    }
}
