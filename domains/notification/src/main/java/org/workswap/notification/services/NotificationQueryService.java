package org.workswap.notification.services;

import java.util.List;

import org.workswap.security.dto.UserAuthData;
import org.workswap.notification.dto.FullNotificationDTO;

public interface NotificationQueryService {
    List<FullNotificationDTO> getUserNotifications(UserAuthData authData);
}
