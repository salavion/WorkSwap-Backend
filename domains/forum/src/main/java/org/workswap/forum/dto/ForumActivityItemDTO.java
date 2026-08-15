package org.workswap.forum.dto;

import java.time.LocalDateTime;

import org.workswap.user.dto.ShortUserDTO;

public record ForumActivityItemDTO(
    String title,
    ShortUserDTO author,
    String link,
    String lang,
    LocalDateTime createdAt,
    String type
) {}
