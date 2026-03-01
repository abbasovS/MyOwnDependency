package com.example.filemanage.service.concrete;

import com.example.filemanage.dto.*;

public interface AuthService {
    ApiMessageResponse signup(SignupRequest request);

    ApiMessageResponse verifyOtp(OtpVerifyRequest request);

    AuthResponse login(LoginRequest request);
}
