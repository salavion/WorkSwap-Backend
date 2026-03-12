package org.workswap.user.services.permission.impl;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;
import org.workswap.user.services.permission.PermissionMappingService;
import org.workswap.user.services.permission.PermissionQueryService;

import jakarta.persistence.EntityNotFoundException;

import org.workswap.user.datasource.model.permission.Permission;
import org.workswap.user.datasource.model.permission.Role;
import org.workswap.user.datasource.repository.permission.PermissionRepository;
import org.workswap.user.datasource.repository.permission.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"production", "statistic"})
@RequiredArgsConstructor
public class PermissionQueryServiceImpl implements PermissionQueryService {
    
    private final PermissionMappingService mappingService;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoleDtos() {
        List<Role> roles = roleRepository.findAll();

        List<RoleDTO> dtos = roles.stream().map(role -> mappingService.convertRoleDTO(role)).toList();;
        return dtos;
    }

    public List<PermissionDTO> getAllPermissionDtos() {
        List<Permission> perms = permissionRepository.findAll();

        List<PermissionDTO> dtos = perms.stream().map(perm -> mappingService.convertPermissionDTO(perm)).toList();
        return dtos;
    }

    public List<PermissionDTO> getPermissionDtosByRole(@NonNull Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
            () -> new EntityNotFoundException("Роль не найдена"));
        Set<Permission> roles = role.getPermissions();
        
        List<PermissionDTO> dtos = roles.stream().map(perm -> mappingService.convertPermissionDTO(perm)).toList();
        return dtos;
    }
}
