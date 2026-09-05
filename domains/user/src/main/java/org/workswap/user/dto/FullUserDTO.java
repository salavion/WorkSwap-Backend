package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FullUserDTO(
    Long id,
    String sub,
    String name,
    String phone,
    String email,
    String bio,
    String avatarUrl,

    List<String> languages,
    List<String> roles,

    Long locationId,
    String avatarType,
    Double rating,
    boolean telegramConnected,
    LocalDateTime createdAt,

    String googleAvatar,
    String uploadedAvatar,
    boolean phoneVisible,
    boolean emailVisible
) {}