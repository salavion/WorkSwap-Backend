package org.workswap.user.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.user.datasource.model.permission.Role;

public record RoleDTO(
    Long id,
    String name,
    int level
) {
    public static RoleDTO ofRole(Role role) {
        return new RoleDTO(role.getId(), role.getName(), role.getLevel());
    }

    public static List<RoleDTO> ofList(Collection<Role> roles) {
        return roles.stream().map(role -> RoleDTO.ofRole(role)).toList();
    }
}