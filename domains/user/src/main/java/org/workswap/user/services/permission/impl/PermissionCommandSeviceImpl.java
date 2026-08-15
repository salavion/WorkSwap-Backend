package org.workswap.user.services.permission.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.user.services.permission.PermissionCommandSevice;
import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;
import org.workswap.user.datasource.repository.permission.PermissionRepository;
import org.workswap.user.datasource.repository.permission.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class PermissionCommandSeviceImpl implements PermissionCommandSevice {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void updateRolePermission(Long roleId, Long permissionId, boolean enabled) {

        if (enabled) {
            roleRepository.addPermissionToRole(roleId, permissionId);
        } else {
            roleRepository.removePermissionFromRole(roleId, permissionId);
        }
    }

    public Role createRole(String roleName) {
        Role role = new Role(roleName, 0);
        return roleRepository.save(role);
    }

    public Permission createPermisson(String permissionName) {
        Permission perm = new Permission(permissionName);
        return permissionRepository.save(perm);
    }
}
