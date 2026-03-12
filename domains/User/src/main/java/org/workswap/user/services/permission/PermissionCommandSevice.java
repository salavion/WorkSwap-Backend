package org.workswap.user.services.permission;

import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;

public interface PermissionCommandSevice {

    void updateRolePermission(Long roleId, Long permissionId, boolean enabled);
    Role createRole(String roleName);
    Permission createPermisson(String permissionName);
}   
