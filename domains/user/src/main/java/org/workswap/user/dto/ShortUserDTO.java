package org.workswap.user.dto;

public record ShortUserDTO(
    Long id,
    String openId,
    String name,
    String avatarUrl
) {}
