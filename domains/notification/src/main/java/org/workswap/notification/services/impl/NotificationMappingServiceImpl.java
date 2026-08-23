package org.workswap.notification.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.notification.datasource.model.Notification;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class NotificationMappingServiceImpl implements NotificationMappingService {
	
    public FullNotificationDTO toDTO(Notification notification) {
        
        return new FullNotificationDTO(
            notification.getId(), 
            notification.getRecipient().getId(), 
            notification.isRead(), 
            notification.getTitle(),
            notification.getContent(), 
            notification.getLink(), 
            notification.getType(), 
            notification.getImportance(), 
            notification.getCreatedAt()
        );
    }
}
