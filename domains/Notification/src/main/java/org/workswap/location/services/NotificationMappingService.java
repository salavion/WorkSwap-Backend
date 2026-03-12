package org.workswap.location.services;

import org.workswap.location.datasource.model.Notification;
import org.workswap.location.dto.FullNotificationDTO;

public interface NotificationMappingService {
    
    FullNotificationDTO toDTO(Notification notification);
}
