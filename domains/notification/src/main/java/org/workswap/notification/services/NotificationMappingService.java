package org.workswap.notification.services;

import org.workswap.notification.datasource.model.Notification;
import org.workswap.notification.dto.FullNotificationDTO;

public interface NotificationMappingService {
    
    FullNotificationDTO toDTO(Notification notification);
}
