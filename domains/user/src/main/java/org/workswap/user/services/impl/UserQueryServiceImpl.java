package org.workswap.user.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.datasource.repository.UserSettingsRepository;
import org.workswap.security.dto.UserAuthData;
import org.workswap.security.enums.UserStatus;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.UserControlPageRequest;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;
import org.workswap.user.services.UserMappingService;
import org.workswap.user.services.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserMappingService userMappingService;

    public User findUserFromOAuth2(OAuth2User oauth2User) {
        User user = userRepository.findByEmail(oauth2User.getAttribute("email")).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
        return user;
    }

    public List<UserDTO> getRecentUsers(int count) {
        List<User> users = userRepository.findAllByStatusOrderByCreatedAtDesc(PageRequest.of(0, count), UserStatus.ACTIVE).getContent();

        return userMappingService.toDTOList(users);
    }

    public boolean checkTelegramConnect(UserAuthData authData) {
        return userSettingsRepository.existsByUserIdAndTelegramConnectedTrue(authData.id());
    }

    public List<User> findAllStandartUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    public UserDTO getCurrentUser(UserAuthData authData) {
        User user = userRepository.getFullUser(authData.id()).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
        return userMappingService.toDTO(user);
    }

    public ShortUserDTO getById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        User user = userRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
        return userMappingService.toShortDTO(user);
    }

    public ShortUserProfileDTO getUserProfile(String userOpenId) {
        User user = userRepository.findByOpenId(userOpenId).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));

        return userMappingService.toShortProfileDTO(user);
    }

    public UserControlPageRequest getUserControlPage(String userOpenId) {
        User user = userRepository.findByOpenId(userOpenId).orElseThrow(
            () -> new IllegalStateException("User not found"));

        FullUserDTO userDto = userMappingService.toFullDto(user);

        return new UserControlPageRequest(userDto);
    }

    public FullUserDTO getFullUserDTO(UserAuthData authData) {
        User user = userRepository.getFullUser(authData.id()).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
        return userMappingService.toFullDto(user);
    }

    public Page<UserDTO> getUsersList(int size, int page, String sortParam) {

        if (sortParam == null || sortParam.length() == 0) sortParam = "createdAt";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam).descending());
        Page<Long> ids = userRepository.findIds(pageable);
        List<User> users = userRepository.findWithRelationsByIds(ids.getContent());

        List<UserDTO> dtos = users.stream().map(u -> userMappingService.toDTO(u)).toList();

        return new PageImpl<>(
            dtos != null ? dtos : new ArrayList<>(), 
            pageable, 
            ids.getTotalElements());
    }
}
