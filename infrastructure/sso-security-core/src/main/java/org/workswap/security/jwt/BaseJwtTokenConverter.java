package org.workswap.security.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.workswap.security.dto.UserAuthData;
import org.workswap.security.enums.UserStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseJwtTokenConverter implements JwtAuthenticationConverter {

    public AbstractAuthenticationToken convert(Jwt jwt) {
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        String userIdStr = jwt.getSubject();
        log.debug("userId: {}", userIdStr);
        Long userId = Long.valueOf(userIdStr);

        String userOpenId = Objects.requireNonNull(jwt.getClaim("openId"));
        UserStatus userStatus = UserStatus.valueOf(Objects.requireNonNull(jwt.getClaim("status")));
        String userName = jwt.getClaim("name");

        UserAuthData authData = new UserAuthData(userId, userOpenId, userName, userStatus);

        return new UserJwtAuthenticationToken(jwt, authorities, authData);
    }
}
