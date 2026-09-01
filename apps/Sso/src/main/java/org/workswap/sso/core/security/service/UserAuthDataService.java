package org.workswap.sso.core.security.service;

import java.util.Objects;

import org.workswap.security.dto.UserAuthData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.sso.datasource.model.User;
import org.workswap.sso.datasource.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthDataService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserAuthData load(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException("Пользователь не найден"));

        return new UserAuthData(
            Objects.requireNonNull(user.getId()),
            Objects.requireNonNull(user.getOpenId()),
            user.getName(),
            Objects.requireNonNull(user.getStatus())
        );
    }
}