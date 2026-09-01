package org.workswap.sso.security.dto;

import java.util.Objects;

import org.workswap.sso.security.enums.UserStatus;

public record UserAuthData(
    Long id,
    String openId,
    String name,
    UserStatus status
) {
    public UserAuthData {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(openId, "openId must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
}