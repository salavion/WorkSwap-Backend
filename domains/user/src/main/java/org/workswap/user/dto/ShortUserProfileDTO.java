package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShortUserProfileDTO(
    Long id,
    String sub,

    String name,
    String phone,
    String email,
    String avatarUrl,
    String bio,

    List<String> languages,
    Double rating,
    LocalDateTime createdAt
) {}
