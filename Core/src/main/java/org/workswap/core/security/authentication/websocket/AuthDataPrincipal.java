package org.workswap.core.security.authentication.websocket;

import java.security.Principal;

import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthDataPrincipal implements Principal {

    private final UserAuthData authData;

    @Override
    public String getName() {
        // имя пользователя для Spring /user/queue/…
        return authData.openId();  
    }

    public UserAuthData getAuthData() {
        return authData;
    }
}
