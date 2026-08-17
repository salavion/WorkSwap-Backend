package org.workswap.notifcation.dto;

import org.workswap.notifcation.enums.NotificationType;
import org.workswap.shared.enums.Importance;

public record NotificationDTO(
    String title,
    String content,
    String link,
    NotificationType type,
    Importance importance
) {}