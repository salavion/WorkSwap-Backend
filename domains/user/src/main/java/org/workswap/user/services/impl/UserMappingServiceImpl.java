package org.workswap.user.services.impl;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Location;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.model.UserSettings;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;
import org.workswap.user.services.UserMappingService;

@Service
@RequiredArgsConstructor
@Profile("server")
public class UserMappingServiceImpl implements UserMappingService {

    public UserDTO toDTO(User user) {

        if (user == null) return null;

        UserSettings settings = user.getSettings();
        boolean phoneVisible = false;
        boolean emailVisible = false;
        if (settings != null) {
            phoneVisible = settings.isPhoneVisible();
            emailVisible = settings.isEmailVisible();
        }
        
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();
                              
        UserDTO dto = new UserDTO(
            user.getId(), 
            user.getSub(), 
            user.getName(), 
            phoneVisible ? user.getPhone() : null, 
            emailVisible ? user.getEmail() : null, 
            user.getBio(), 
            user.getAvatarUrl(),
            user.getLanguages(),
            roles,
            user.getRating(),
            user.getCreatedAt()
        );
        return dto;
    }

    public FullUserDTO toFullDto(User user) {

        if (user == null) return null;

        Long locationId = Optional
            .ofNullable(user.getLocation())
            .map(Location::getId)
            .orElse(null);
            
        UserSettings settings = user.getSettings();

        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

        FullUserDTO dto = new FullUserDTO(
            user.getId(),
            user.getSub(),
            user.getName(),
            user.getPhone(),
            user.getEmail(),
            user.getBio(),
            user.getAvatarUrl(),
            user.getLanguages(),
            roles,
            locationId,
            settings.getAvatarType(),
            user.getRating(),
            settings.isTelegramConnected(),
            user.getCreatedAt(),
            settings.getGoogleAvatar(),
            settings.getUploadedAvatar(),
            settings.isPhoneVisible(),
            settings.isEmailVisible()
        );

        return dto;
    }

    public ShortUserDTO toShortDTO(User user) {
        return new ShortUserDTO(user.getId(), user.getSub(), user.getName(), user.getAvatarUrl());
    }

    public ShortUserProfileDTO toShortProfileDTO(User user) {

        UserSettings settings = user.getSettings();
        boolean phoneVisible = false;
        boolean emailVisible = false;
        if (settings != null) {
            phoneVisible = settings.isPhoneVisible();
            emailVisible = settings.isEmailVisible();
        }
        
        return new ShortUserProfileDTO(
            user.getId(),
            user.getSub(), 
            user.getName(), 
            phoneVisible ? user.getPhone() : null, 
            emailVisible ? user.getEmail() : null, 
            user.getAvatarUrl(), 
            user.getBio(),
            user.getLanguages(), 
            user.getRating(), 
            user.getCreatedAt());
    }

    public List<UserDTO> toDTOList(Collection<User> users) {
        return users.stream().map(user -> toDTO(user)).toList();
    }

    public List<ShortUserDTO> toShortDTOList(Collection<User> users) {
        return users.stream().map(user -> toShortDTO(user)).toList();
    }
}

