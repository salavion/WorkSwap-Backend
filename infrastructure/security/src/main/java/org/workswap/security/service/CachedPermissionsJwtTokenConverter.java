package org.workswap.security.service;

import java.util.Collection;
import java.util.Objects;

import org.workswap.security.dto.UserAuthData;
import org.workswap.security.enums.UserStatus;
import org.workswap.security.jwt.JwtAuthenticationConverter;
import org.workswap.security.jwt.UserJwtAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CachedPermissionsJwtTokenConverter implements JwtAuthenticationConverter {

    private final PermissionsService permissionsService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Long userId = Objects.requireNonNull(Long.valueOf(jwt.getSubject()));

        Collection<GrantedAuthority> authorities =
            permissionsService.getUserPermissions(userId);

        UserAuthData authData = new UserAuthData(
            Objects.requireNonNull(userId),
            Objects.requireNonNull(jwt.getClaim("openId")),
            jwt.getClaim("name"),
            UserStatus.valueOf(jwt.getClaim("status"))
        );

        return new UserJwtAuthenticationToken(jwt, authorities, authData);
    }
}
