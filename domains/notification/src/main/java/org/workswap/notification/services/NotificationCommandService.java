package org.workswap.notification.services;

import org.workswap.security.dto.UserAuthData;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.order.datasource.model.Order;
import org.workswap.shared.events.notification.CreateNotificationCommand;

public interface NotificationCommandService {
    
    void markAsRead(UserAuthData authData, Long notificationId);
    FullNotificationDTO saveNotification(CreateNotificationCommand event);

    /* void sendNewsNotification(News news); */
    void sendOrderCompleteNotification(Order order);

    void sendNotification(CreateNotificationCommand event);
}
