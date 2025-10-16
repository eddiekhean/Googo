package com.example.userservice.service;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    void register(RegisterRequest request) throws BadRequestException;
    void sendOtp() ;
    boolean verifyOtp(@Valid @RequestParam String otp);

    AuthResponse refreshToken(String refreshToken);
}
