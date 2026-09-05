package org.workswap.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;
import org.workswap.user.datasource.repository.permission.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionsService {

    private final RoleRepository roleRepository;

    @Cacheable(
        value = "user-permissions",
        key = "#userId"
    )
    public Collection<GrantedAuthority> getUserPermissions(String userSub) {

        Set<Role> roles = roleRepository.findRolesWithPermissionsByUserSub(userSub);

        Set<GrantedAuthority> authorities = new HashSet<>();

        // permissions
        roles.stream()
            .filter(role -> role.getPermissions() != null)
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);

        // roles with ROLE_
        roles.stream()
            .map(Role::getName)
            .map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);

        return authorities;
    }
}