package org.workswap.user.services.permission;

import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;
import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;

public interface PermissionMappingService {
    
    PermissionDTO convertPermissionDTO(Permission perm);
    RoleDTO convertRoleDTO(Role role);
}
