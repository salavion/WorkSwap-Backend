package org.workswap.user.services;

import java.util.Map;

import org.salavion.security.dto.UserAuthData;
import org.springframework.lang.NonNull;

public interface UserCommandService {

    String connectTelegram(UserAuthData authData);

    void deleteUser(UserAuthData authData);
    
    void acceptTerms(UserAuthData authData);

    void modifyUserParam(UserAuthData authData, Map<String, Object> updates);
    void createUser(@NonNull Long userId);
}