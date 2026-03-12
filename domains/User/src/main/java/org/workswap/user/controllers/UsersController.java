package org.workswap.user.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.salavion.security.dto.UserAuthData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.user.services.UserCommandService;
import org.workswap.user.services.UserQueryService;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.UserDTO;
/* import org.workswap.user.dto.UserControlPageRequest;
import org.workswap.user.dto.ProfilePageRequest; */

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/telegram")
    @PreAuthorize("hasAuthority('CONNECT_TELEGRAM')")
    public String telegramConnect(@AuthenticationPrincipal UserAuthData authData) {
        return userCommandService.connectTelegram(authData);
    }

    @GetMapping("/telegram")
    @PreAuthorize("hasAuthority('CONNECT_TELEGRAM')")
    public Boolean checkTelegramConnect(@AuthenticationPrincipal UserAuthData authData) {
        return userQueryService.checkTelegramConnect(authData);
    }

    @PostMapping("/accept-terms")
    @PreAuthorize("hasAuthority('ACCEPT_TERMS')")
    public void acceptTerms(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.acceptTerms(authData);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('DELETE_OWN_ACCOUNT')")
    public void deleteAccount(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.deleteUser(authData);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GET_CURRENT_USER')")
    public UserDTO getCurrentUser(@AuthenticationPrincipal UserAuthData authData) {
        return userQueryService.getCurrentUser(authData);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAuthority('GET_CURRENT_USER_SETTINGS')")
    public FullUserDTO getCurrentUserSettings(@AuthenticationPrincipal UserAuthData authData) {
        return userQueryService.getFullUserDTO(authData);
    }

    @GetMapping("/{userId}")
    @PermitAll
    public ShortUserDTO getUser(@PathVariable Long userId) {
        return userQueryService.getById(userId);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('GET_RECENT_USERS')")
    public List<UserDTO> getRecentUsers(@RequestParam int amount) {
        return userQueryService.getRecentUsers(amount);
    }

    @PatchMapping("/modify")
    @PreAuthorize("hasAuthority('UPDATE_USER_SETTINGS')")
    public void modifyUser(
        @AuthenticationPrincipal UserAuthData authData,
        @RequestBody Map<String, Object> updates
    ) {
        userCommandService.modifyUserParam(authData, updates);
    }

    /* @GetMapping("/{userOpenId}/profile")
    @PermitAll
    public ProfilePageRequest getUserProfile(@PathVariable String userOpenId, @RequestParam String locale) {
        return userQueryService.getUserProfile(userOpenId, Locale.of(locale));
    }

    @GetMapping("/{userOpenId}/full-info")
    @PreAuthorize("hasAuthority('GET_FULL_USER_INFO')")
    public UserControlPageRequest getUserControlPage(
        @PathVariable String userOpenId,
        @RequestParam String locale
    ) {
        return userQueryService.getUserControlPage(userOpenId, Locale.of(locale));
    } */
}
