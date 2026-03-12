package org.workswap.location.dto;

import java.time.LocalDateTime;

public record FullNotificationDTO(
    Long id,
    Long recipientId,
    boolean isRead,
    String title,
    String content,
    String link,
    String type,
    String importance,
    LocalDateTime createdAt
) {}
