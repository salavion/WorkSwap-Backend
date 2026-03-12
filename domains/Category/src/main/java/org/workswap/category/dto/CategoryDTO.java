package org.workswap.category.dto;

public record CategoryDTO(
    Long id,
    String name,
    Long parentId,
    boolean leaf
) {}
