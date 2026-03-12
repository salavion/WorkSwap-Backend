package org.workswap.user.services;

import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;

import java.util.Collection;
import java.util.List;

import org.workswap.user.datasource.model.User;

public interface UserMappingService {

    UserDTO toDTO(User user);
    ShortUserDTO toShortDTO(User user);
    FullUserDTO toFullDto(User user);
    ShortUserProfileDTO toShortProfileDTO(User user);
    List<UserDTO> toDTOList(Collection<User> users);
    List<ShortUserDTO> toShortDTOList(Collection<User> users);
}
