package org.workswap.user.dto;

public record RoleDTO(
    Long id,
    String name,
    int level
) {}