package com.example.filemanage;

import com.example.filemanage.dto.LoginRequest;
import com.example.filemanage.dto.OtpVerifyRequest;
import com.example.filemanage.dto.SignupRequest;
import com.example.filemanage.model.OtpVerificationEntity;
import com.example.filemanage.model.UserEntity;
import com.example.filemanage.repository.OtpVerificationRepository;
import com.example.filemanage.repository.UserRepository;
import com.example.filemanage.service.impl.AuthServiceImpl;
import com.example.filemanage.service.impl.JwtService;
import com.example.filemanage.service.impl.OtpEmailEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpVerificationRepository otpVerificationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OtpEmailEventPublisher otpEmailEventPublisher;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        UserEntity user = UserEntity.builder()
                .email("test@mail.com")
                .passwordHash("hashed")
                .enabled(true)
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token");

        var response = authService.login(new LoginRequest("test@mail.com", "123456"));

        assertEquals("token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void verifyOtp_ShouldEnableUser_WhenOtpValid() {
        UserEntity user = UserEntity.builder().email("test@mail.com").enabled(false).build();
        OtpVerificationEntity otp = OtpVerificationEntity.builder()
                .email("test@mail.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        when(otpVerificationRepository.findTopByEmailAndOtpCodeAndUsedIsFalseOrderByCreatedAtDesc("test@mail.com", "123456"))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        var response = authService.verifyOtp(new OtpVerifyRequest("test@mail.com", "123456"));

        assertTrue(user.isEnabled());
        assertTrue(otp.isUsed());
        assertNotNull(response.message());
    }

    @Test
    void signup_ShouldPublishOtp_WhenRequestValid() {
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userRepository.existsByUsername("tester")).thenReturn(false);

        authService.signup(new SignupRequest("tester", "test@mail.com", "secret123"));

        verify(userRepository).save(any(UserEntity.class));
        verify(otpVerificationRepository).save(any(OtpVerificationEntity.class));
        verify(otpEmailEventPublisher).publish(eq("test@mail.com"), anyString());
    }
}
