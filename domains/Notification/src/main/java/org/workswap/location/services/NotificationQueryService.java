package org.workswap.location.services;

import java.util.List;

import org.salavion.security.dto.UserAuthData;
import org.workswap.location.dto.FullNotificationDTO;

public interface NotificationQueryService {
    List<FullNotificationDTO> getUserNotifications(UserAuthData authData);
}
