package org.workswap.user.services;

import java.util.List;
import java.util.Locale;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.workswap.user.datasource.model.User;
import org.salavion.security.dto.UserAuthData;
import org.workswap.user.dto.FullUserDTO;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.UserDTO;
/* import org.workswap.user.dto.ProfilePageRequest;
import org.workswap.user.dto.UserControlPageRequest; */

public interface UserQueryService {
    
    User findUserFromOAuth2(OAuth2User oauth2User);
    boolean checkTelegramConnect(UserAuthData authData);

    List<User> findAllStandartUsers();
    
    List<UserDTO> getRecentUsers(int count);
    FullUserDTO getFullUserDTO(UserAuthData authData);

    UserDTO getCurrentUser(UserAuthData authData);
    ShortUserDTO getById(Long userId);
    /* ProfilePageRequest getUserProfile(String userOpenId, Locale locale);
    UserControlPageRequest getUserControlPage(String userOpenId, Locale locale); */
}
