package org.workswap.subscription.dto;

import java.time.LocalDateTime;

public record SubscriptionDTO(
    Long id,
    Long userId,
    String type,
    Long targetId,
    LocalDateTime createdAt
) {}
