package org.workswap.notifcation.controllers;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.workswap.notifcation.dto.FullNotificationDTO;
import org.workswap.notifcation.services.NotificationQueryService;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final NotificationQueryService notificationQueryService;
 
    @MessageMapping("/notifications.loadNotifications")
    @SendToUser("/queue/notifications/history.notifications")
    public List<FullNotificationDTO> loadMessagesForChat(
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return notificationQueryService.getUserNotifications(authData);
    }
}
