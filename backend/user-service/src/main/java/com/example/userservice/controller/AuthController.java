package com.example.userservice.controller;

import com.example.userservice.dto.AuthResponse;
import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    /**
     * Register new user
     * - Validate uniqueness (email, username)
     * - Hash password and save user
     * - Set default fields (role, gender, status)
     * - Send welcome/verification email
     */
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) throws BadRequestException {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully. Please verify your email."));
    }
    /**
     * Login
     * - Authenticate username + password
     * - Generate access + refresh tokens
     * - Store refresh token in Redis (7 days)
     * - Return AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    /**
     * Verify email with OTP
     * - Accept OTP input
     * - Validate OTP against Redis
     * - Delete OTP if valid
     * - Mark user as active
     */
    @PostMapping("/sendOTP")
    ResponseEntity<?> sendOtp() {
        authService.sendOtp();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Check your email!"));
    }
    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail(@Valid @RequestBody String otp) {
        authService.verifyOtp(otp);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Email đã được xác thực"));
    }
    /**
     * Refresh access token
     * - Accept refresh token
     * - Verify signature & expiration
     * - Validate against Redis
     * - Issue new access + refresh tokens
     * - Update Redis with new refresh token
     */
    @PostMapping("/refresh-token")
    ResponseEntity<?> refreshToken(String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    /**
     * Logout
     * - Accept refresh token from client
     * - Invalidate access token (store in Redis blacklist until expiry)
     * - Delete refresh token from Redis
     */
    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody String refreshToken) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authService.logout(authentication.getCredentials().toString(),refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    /**
     * Resend verification OTP
     * - Check if user exists and not activated
     * - Generate a new OTP
     * - Store in Redis (TTL = 5 minutes)
     * - Send email again
     */
    @PostMapping("/resend-otp")
    ResponseEntity<?> resendOtp() {
        authService.sendOtp();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Check your email!"));
    }

    /**
     * Forgot password
     * - Accept email or username
     * - Generate OTP for password reset
     * - Store in Redis (TTL = 5 minutes)
     * - Send OTP email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("identifier") String identifier) {
        if (EMAIL_PATTERN.matcher(identifier).matches()) {
            authService.forgotPasswordEmail(identifier);
        } else {
            authService.forgotPasswordUserName(identifier);
        }
        return ResponseEntity.ok(Map.of("message", "Password reset OTP sent"));
    }

    /**
     * Reset password (using OTP)
     * - Accept email + OTP + new password
     * - Verify OTP in Redis
     * - Hash and update new password
     * - Delete OTP from Redis
     */
    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@RequestBody String email, String newPassword,String otp) {
        authService.resetPassword(email,otp, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    /**
     * Change password (while logged in)
     * - Extract user from JWT
     * - Verify old password
     * - Encode and update new password
     */
    @PutMapping("/change-password")
    ResponseEntity<?> changePassword(@RequestBody String oldPassword,  String newPassword) {
        authService.changePassword(oldPassword,newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    /**
     * OAuth2 Login (Google, GitHub, etc.)
     * - Redirect to OAuth2 provider login page
     */
    @GetMapping("/oauth2/authorize/{provider}")
    ResponseEntity<?> oAuth2(@PathVariable("provider") String provider) {
        // TODO: redirect to provider's OAuth2 page
        // - generate authorization URL
        // - return redirect URL
        return ResponseEntity.ok(Map.of("authUrl", "https://accounts.google.com/..."));
    }

    /**
     * OAuth2 Callback
     * - Receive authorization code from provider
     * - Exchange code for provider accessToken
     * - Fetch user profile info
     * - Create or login user locally
     * - Generate internal JWT tokens
     */
    @GetMapping("/oauth2/callback/{provider}")
    ResponseEntity<?> callback(@PathVariable("provider") String provider, @RequestParam String code) {
        // TODO: handle OAuth2 callback
        // - exchange code for accessToken
        // - fetch provider user info (email, name, avatar)
        // - find or create local user
        // - generate JWT access + refresh tokens
        return ResponseEntity.ok(Map.of("message", "OAuth2 login successful"));
    }
}

