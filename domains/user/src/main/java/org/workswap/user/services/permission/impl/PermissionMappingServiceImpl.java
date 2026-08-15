package org.workswap.user.services.permission.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;
import org.workswap.user.services.permission.PermissionMappingService;
import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"production", "statistic"})
@RequiredArgsConstructor
public class PermissionMappingServiceImpl implements PermissionMappingService {
    
    public PermissionDTO convertPermissionDTO(Permission perm) {
        return new PermissionDTO(perm.getId(), perm.getName(), perm.getComment());
    }

    public RoleDTO convertRoleDTO(Role role) {
        return new RoleDTO(role.getId(), role.getName(), role.getLevel());
    }
}
