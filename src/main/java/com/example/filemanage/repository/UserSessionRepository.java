package com.example.filemanage.repository;

import com.example.filemanage.model.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {
    Optional<UserSessionEntity> findByTokenAndExpiresAtAfter(String token, LocalDateTime now);
}
