package org.workswap.user.controllers;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.datasource.repository.permission.PermissionRepository;
import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;
import org.workswap.user.services.permission.PermissionCommandSevice;
import org.workswap.user.services.permission.PermissionQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionQueryService permissionQueryService;
    private final PermissionCommandSevice permissionCommandSevice;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_ALL_PERMISSIONS')")
    public List<PermissionDTO> getPermissions() {
        return permissionQueryService.getAllPermissionDtos();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('GET_ALL_ROLES')")
    public List<RoleDTO> getRoles() {
        return permissionQueryService.getAllRoleDtos();
    }

    @GetMapping("/{roleId}/get")
    @PreAuthorize("hasAuthority('GET_PERMISSIONS_BY_ROLE')")
    public List<PermissionDTO> getPermissionsByRole(@NonNull @PathVariable Long roleId) {
        return permissionQueryService.getPermissionDtosByRole(roleId);
    }

    @PutMapping("/{roleId}/role")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public void savePermissionsForRole(
        @PathVariable Long roleId,
        @RequestParam Long permissionId,
        @RequestParam boolean enabled
    ) {
        permissionCommandSevice.updateRolePermission(roleId, permissionId, enabled);
    }

    @PostMapping("/role")
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public Long createRole(@RequestParam String roleName) {
        return permissionCommandSevice.createRole(roleName).getId();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PERMISSION')")
    public Long createPermission(@RequestParam String permissionName) {
        return permissionCommandSevice.createPermisson(permissionName).getId();
    }

    @PatchMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('UPDATE_PERMISSION')")
    public void updatePermission(
        @PathVariable Long permissionId,
        @RequestParam(required = false) String name, 
        @RequestParam(required = false) String comment
    ) {
        permissionRepository.updatePermission(permissionId, name, comment);
    }

    @PostMapping("/user/role")
    @PreAuthorize("hasAuthority('ADD_USER_ROLE')")
    public void userAddRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userRepository.addRoleToUser(userId, roleId);
    }

    @DeleteMapping("/user/role")
    @PreAuthorize("hasAuthority('REMOVE_USER_ROLE')")
    public void userRemoveRole(@RequestParam Long userId, @RequestParam Long roleId) {
        userRepository.removeRoleFromUser(userId, roleId);
    }
}