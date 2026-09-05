package org.workswap.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDTO(
    String sub,

    String name,
    String phone,
    String email,

    String bio,
    String avatarUrl,

    List<String> languages,
    List<String> roles,

    Double rating,
    LocalDateTime createdAt
) {}
