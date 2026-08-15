package org.workswap.location.services;

import org.salavion.security.dto.UserAuthData;
import org.workswap.location.dto.FullNotificationDTO;
import org.workswap.location.dto.NotificationDTO;
import org.workswap.order.datasource.model.Order;
import org.workswap.user.datasource.model.User;

public interface NotificationCommandService {
    
    void markAsRead(UserAuthData authData, Long notificationId);
    FullNotificationDTO saveChatNotification(Long userId, NotificationDTO notification);

    /* void sendNewsNotification(News news); */
    void sendOrderCompleteNotification(Order order);

    void sendNotification(User receiver, NotificationDTO notificationDto);
}
