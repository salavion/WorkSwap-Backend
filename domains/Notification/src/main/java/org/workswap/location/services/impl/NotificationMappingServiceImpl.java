package org.workswap.location.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Notification;
import org.workswap.location.dto.FullNotificationDTO;
import org.workswap.location.services.NotificationMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class NotificationMappingServiceImpl implements NotificationMappingService {
	
    public FullNotificationDTO toDTO(Notification notification) {
        
        return new FullNotificationDTO(notification.getId(), 
                                       notification.getRecipient().getId(), 
                                       notification.isRead(), 
                                       notification.getTitle(),
                                       notification.getContent(), 
                                       notification.getLink(), 
                                       notification.getType().toString(), 
                                       notification.getImportance().toString(), 
                                       notification.getCreatedAt());
    }
}
