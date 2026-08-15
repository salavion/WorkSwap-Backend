package org.workswap.user.services.permission;

import java.util.List;

import org.springframework.lang.NonNull;
import org.workswap.user.dto.PermissionDTO;
import org.workswap.user.dto.RoleDTO;

public interface PermissionQueryService {
    List<RoleDTO> getAllRoleDtos();
    List<PermissionDTO> getAllPermissionDtos();
    List<PermissionDTO> getPermissionDtosByRole(@NonNull Long roleId);
}
