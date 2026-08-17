package org.workswap.notifcation.services;

import org.workswap.notifcation.datasource.model.Notification;
import org.workswap.notifcation.dto.FullNotificationDTO;

public interface NotificationMappingService {
    
    FullNotificationDTO toDTO(Notification notification);
}
