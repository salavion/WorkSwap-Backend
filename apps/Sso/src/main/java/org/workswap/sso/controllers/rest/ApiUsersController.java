package org.workswap.sso.controllers.rest;

import java.util.Map;

import org.workswap.security.annotations.controllers.Authenticated;
import org.workswap.security.annotations.controllers.PublicEndpoint;
import org.workswap.security.dto.UserAuthData;
import org.workswap.security.dto.UserInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.core.user.UserCommandService;
import org.workswap.sso.core.user.UserQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ApiUsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Value("${salavion.code}")
    private String salavionCode;
    
    @PostMapping("/accept-terms")
    @Authenticated
    public void acceptTerms(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.acceptTerms(authData);
    }

    @DeleteMapping("/current/delete")
    @Authenticated
    public void deleteAccount(@AuthenticationPrincipal UserAuthData authData) {
        userCommandService.deleteUser(authData);
    }

    @PatchMapping("/modify")
    @Authenticated
    public void modifyUser(
        @AuthenticationPrincipal UserAuthData authData,
        @RequestBody Map<String, Object> updates
    ) {
        userCommandService.modifyUserParam(authData, updates);
    }

    @GetMapping("/info/{id}")
    @PublicEndpoint
    public UserInfoDTO getUserInfo(@PathVariable Long id, @RequestHeader("X-SALAVION-CODE") String code) {

        if (code != null && !code.equals(salavionCode)) {
            throw new AccessDeniedException("Этот запрос недоступен пользователям");
        }
        
        return userQueryService.getUserInfo(id);
    }
}
