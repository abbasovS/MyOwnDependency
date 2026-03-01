package com.example.filemanage.service.impl;

import com.example.filemanage.model.UserEntity;
import com.example.filemanage.model.UserSessionEntity;
import com.example.filemanage.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserSessionRepository userSessionRepository;

    @Value("${auth.jwt-expiration-minutes}")
    private long tokenExpirationMinutes;

    public String generateToken(UserEntity user) {
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        UserSessionEntity session = UserSessionEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                .build();
        userSessionRepository.save(session);
        return token;
    }

    public UserEntity resolveUser(String token) {
        return userSessionRepository.findByTokenAndExpiresAtAfter(token, LocalDateTime.now())
                .map(UserSessionEntity::getUser)
                .orElse(null);
    }
}
