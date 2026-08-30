package org.workswap.notification.controllers;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.workswap.notification.dto.FullNotificationDTO;
import org.workswap.notification.services.NotificationQueryService;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

// TODO решить вопрос с защитой вебсокетных контроллеров
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
