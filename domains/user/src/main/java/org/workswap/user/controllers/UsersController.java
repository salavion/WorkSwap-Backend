package org.workswap.user.controllers;

import java.util.List;
import java.util.Map;

import org.workswap.security.annotations.controllers.PublicEndpoint;
import org.workswap.security.annotations.controllers.RequiredPermission;
import org.workswap.security.annotations.parameters.AuthUser;
import org.workswap.security.dto.UserAuthData;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
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
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.dto.UserControlPageRequest;
import org.workswap.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/user")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/telegram")
    @RequiredPermission("CONNECT_TELEGRAM")
    public String telegramConnect(@AuthUser UserAuthData authData) {
        return userCommandService.connectTelegram(authData);
    }

    @GetMapping("/telegram")
    @RequiredPermission("CONNECT_TELEGRAM")
    public Boolean checkTelegramConnect(@AuthUser UserAuthData authData) {
        return userQueryService.checkTelegramConnect(authData);
    }

    @PostMapping("/accept-terms")
    @RequiredPermission("ACCEPT_TERMS")
    public void acceptTerms(@AuthUser UserAuthData authData) {
        userCommandService.acceptTerms(authData);
    }

    @DeleteMapping
    @RequiredPermission("DELETE_OWN_ACCOUNT")
    public void deleteAccount(@AuthUser UserAuthData authData) {
        userCommandService.deleteUser(authData);
    }

    @GetMapping
    @RequiredPermission("GET_CURRENT_USER")
    public UserDTO getCurrentUser(@AuthUser UserAuthData authData) {
        return userQueryService.getCurrentUser(authData);
    }

    @GetMapping("/settings")
    @RequiredPermission("GET_CURRENT_USER_SETTINGS")
    public FullUserDTO getCurrentUserSettings(@AuthUser UserAuthData authData) {
        return userQueryService.getFullUserDTO(authData);
    }

    @GetMapping("/{userId}")
    @PublicEndpoint
    public ShortUserDTO getUser(@PathVariable Long userId) {
        return userQueryService.getById(userId);
    }

    @GetMapping("/recent")
    @RequiredPermission("GET_RECENT_USERS")
    public List<UserDTO> getRecentUsers(@RequestParam int amount) {
        return userQueryService.getRecentUsers(amount);
    }

    @PatchMapping("/modify")
    @RequiredPermission("UPDATE_USER_SETTINGS")
    public void modifyUser(
        @AuthUser UserAuthData authData,
        @RequestBody Map<String, Object> updates
    ) {
        userCommandService.modifyUserParam(authData, updates);
    }

    @GetMapping("/{userOpenId}/profile")
    @PublicEndpoint
    public ShortUserProfileDTO getUserProfile(@PathVariable String userOpenId) {
        return userQueryService.getUserProfile(userOpenId);
    }

    @GetMapping("/{userOpenId}/full-info")
    @RequiredPermission("GET_FULL_USER_INFO")
    public UserControlPageRequest getUserControlPage(
        @PathVariable String userOpenId
    ) {
        return userQueryService.getUserControlPage(userOpenId);
    }

    @GetMapping("/list")
    @RequiredPermission("GET_USERS_LIST")
    public Page<UserDTO> getUsersList(
        @RequestParam int size, 
        @RequestParam int page, 
        @RequestParam String sortParam
    ) {
        return userQueryService.getUsersList(size, page, sortParam);
    }
}
