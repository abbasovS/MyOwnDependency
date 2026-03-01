package com.example.filemanage.repository;

import com.example.filemanage.model.OtpVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepository extends JpaRepository<OtpVerificationEntity, UUID> {
    Optional<OtpVerificationEntity> findTopByEmailAndOtpCodeAndUsedIsFalseOrderByCreatedAtDesc(String email, String otpCode);
}
