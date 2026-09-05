package org.workswap.user.dto;

public record ShortUserDTO(
    Long id,
    String sub,
    String name,
    String avatarUrl
) {}
