package org.workswap.user.services.permission.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;
import org.workswap.user.services.permission.PermissionQueryService;

import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;
import org.workswap.user.datasource.repository.permission.PermissionRepository;
import org.workswap.user.datasource.repository.permission.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server", "statistic"})
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoleDtos() {
        List<Role> roles = roleRepository.findAll();

        return RoleDTO.ofList(roles);
    }

    public List<PermissionDTO> getAllPermissionDtos() {
        List<Permission> perms = permissionRepository.findAll();

        return PermissionDTO.ofList(perms);
    }

    public List<PermissionDTO> getPermissionDtosByRole(Long roleId) {
        List<Permission> perms = permissionRepository.findByRole(roleId);
        
        return PermissionDTO.ofList(perms);
    }
}
