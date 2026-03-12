package org.workswap.order.dto;

import java.time.LocalDateTime;

public record OrderDTO(
    String id,
    Long listingId,
    Long buyerId,
    Long sellerId,
    Long chatId,

    String status,
    boolean confirmedByBuyer,
    boolean confirmedBySeller,

    LocalDateTime createdAt
) {}
