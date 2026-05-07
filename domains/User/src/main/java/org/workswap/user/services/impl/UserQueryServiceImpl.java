package org.workswap.user.services.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.datasource.repository.UserSettingsRepository;
import org.salavion.security.dto.UserAuthData;
import org.salavion.security.enums.UserStatus;
import org.workswap.user.dto.FullUserDTO;
/* import org.workswap.user.dto.ProfilePageRequest;
import org.workswap.user.dto.UserControlPageRequest; */
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserDTO;
import org.workswap.user.services.UserMappingService;
import org.workswap.user.services.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final UserMappingService userMappingService;
    /* private final ListingMappingService listingMappingService;
    private final ReviewMappingService reviewMappingService;
    private final ForumQueryService forumQueryService; */

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

    /* public UserControlPageRequest getUserControlPage(String userOpenId, Locale locale) {
        User user = userRepository.findByOpenId(userOpenId).orElseThrow(
            () -> new IllegalStateException("User not found"));

        FullUserDTO userDto = userMappingService.toFullDto(user);

        List<ShortListingDTO> listings = listingMappingService.getUserShortListings(user, locale);
        List<ReviewDTO> reviews = user.getReviews().stream().map(r -> reviewMappingService.toDTO(r)).toList();

        UserForumContent forumContent = forumQueryService.getUserForumContent(user.getId());

        return new UserControlPageRequest(userDto, listings, reviews, forumContent);
    } */

    public FullUserDTO getFullUserDTO(UserAuthData authData) {
        User user = userRepository.getFullUser(authData.id()).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
        return userMappingService.toFullDto(user);
    }
}
