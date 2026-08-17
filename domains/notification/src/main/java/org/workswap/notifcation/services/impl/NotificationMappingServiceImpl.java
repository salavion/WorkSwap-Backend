package org.workswap.notifcation.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.notifcation.datasource.model.Notification;
import org.workswap.notifcation.dto.FullNotificationDTO;
import org.workswap.notifcation.services.NotificationMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
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
