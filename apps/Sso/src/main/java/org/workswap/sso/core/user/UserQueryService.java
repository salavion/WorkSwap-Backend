package org.workswap.sso.core.user;

import org.springframework.stereotype.Service;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.repository.UserRepository;
import org.workswap.sso.security.dto.UserInfoDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;
    
    public UserInfoDTO getUserInfo(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));
            
        return new UserInfoDTO(user.getId(), user.getOpenId(), user.getName(), user.getEmail(), user.getAvatarUrl(), user.getStatus());
    }
}
