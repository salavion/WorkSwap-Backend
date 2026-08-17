package org.workswap.notifcation.services;

import java.util.List;

import org.salavion.security.dto.UserAuthData;
import org.workswap.notifcation.dto.FullNotificationDTO;

public interface NotificationQueryService {
    List<FullNotificationDTO> getUserNotifications(UserAuthData authData);
}
