package com.example.filemanage.service.impl;

import com.example.filemanage.dto.*;
import com.example.filemanage.model.OtpVerificationEntity;
import com.example.filemanage.model.UserEntity;
import com.example.filemanage.repository.OtpVerificationRepository;
import com.example.filemanage.repository.UserRepository;
import com.example.filemanage.service.concrete.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpEmailEventPublisher otpEmailEventPublisher;
    private final JwtService jwtService;

    @Value("${auth.otp-expiration-minutes}")
    private int otpExpirationMinutes;

    @Override
    @Transactional
    public ApiMessageResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .enabled(false)
                .build();
        userRepository.save(user);

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        OtpVerificationEntity otpEntity = OtpVerificationEntity.builder()
                .email(request.email())
                .otpCode(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .used(false)
                .build();
        otpVerificationRepository.save(otpEntity);
        otpEmailEventPublisher.publish(request.email(), otp);

        return new ApiMessageResponse("Signup tamamlandı, OTP email-ə göndərildi");
    }

    @Override
    @Transactional
    public ApiMessageResponse verifyOtp(OtpVerifyRequest request) {
        OtpVerificationEntity otp = otpVerificationRepository
                .findTopByEmailAndOtpCodeAndUsedIsFalseOrderByCreatedAtDesc(request.email(), request.otpCode())
                .orElseThrow(() -> new BadCredentialsException("OTP not found"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP expired");
        }

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        user.setEnabled(true);
        otp.setUsed(true);

        return new ApiMessageResponse("OTP təsdiqləndi, login edə bilərsiniz");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Email və ya şifrə yanlışdır"));

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Email OTP ilə təsdiqlənməyib");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email və ya şifrə yanlışdır");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer");
    }
}
