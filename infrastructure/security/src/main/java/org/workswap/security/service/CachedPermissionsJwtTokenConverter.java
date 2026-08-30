package org.workswap.security.service;

import java.util.Collection;
import java.util.Objects;

import org.salavion.security.dto.UserAuthData;
import org.salavion.security.enums.UserStatus;
import org.salavion.security.jwt.JwtAuthenticationConverter;
import org.salavion.security.jwt.UserJwtAuthenticationToken;
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
