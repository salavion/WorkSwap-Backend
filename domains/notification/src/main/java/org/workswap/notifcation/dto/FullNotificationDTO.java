package org.workswap.notifcation.dto;

import java.time.LocalDateTime;

import org.workswap.notifcation.enums.NotificationType;
import org.workswap.shared.enums.Importance;

public record FullNotificationDTO(
    Long id,
    Long recipientId,
    boolean isRead,
    String title,
    String content,
    String link,
    NotificationType type,
    Importance importance,
    LocalDateTime createdAt
) {}
