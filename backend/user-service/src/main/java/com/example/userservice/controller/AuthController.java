package com.example.userservice.controller;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.dto.UserDetailsImpl;
import com.example.userservice.service.AuthService;
import com.example.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Register a new user account
     * - Check for duplicate email/username
     * - Hash the password
     * - Save the user as INACTIVE
     * - Generate OTP and store it in Redis (TTL = 5 minutes)
     * - Send verification email
     */
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) throws BadRequestException {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully. Please verify your email."));
    }

    /**
     * User login
     * - Authenticate by email + password
     * - Check if user status = ACTIVE
     * - Generate accessToken and refreshToken
     * - Store refreshToken in Redis (TTL = 7 days)
     * - Return token response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        String key = "refresh_token::" + user.getId();
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "expiresIn", jwtUtil.getAccessExpiration()
        ));
    }

    /**
     * Verify email by OTP
     * - Accept email + OTP
     * - Check OTP in Redis
     * - If valid → activate user (status = ACTIVE)
     * - Delete OTP from Redis
     */
    @PostMapping("/verify-email")
    ResponseEntity<?> verifyEmail() {
        // TODO: verify email by OTP
        // - validate OTP from Redis
        // - if valid: activate user
        // - else: throw InvalidOtpException
        return ResponseEntity.ok().build();
    }

    /**
     * Refresh access token
     * - Accept refreshToken
     * - Validate refreshToken from Redis
     * - Generate a new accessToken
     * - Return the new accessToken
     */
    @PostMapping("/refresh-token")
    ResponseEntity<?> refreshToken() {
        // TODO: implement refresh token logic
        // - validate refreshToken
        // - generate and return new accessToken
        return ResponseEntity.ok().build();
    }

    /**
     * Logout user
     * - Remove refreshToken from Redis
     * - Optionally blacklist the accessToken
     * - Return success message
     */
    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // TODO: revoke JWT tokens
        // - remove refreshToken from Redis
        // - optionally add accessToken to blacklist
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
        // TODO: resend verification OTP
        // - verify user exists and inactive
        // - generate new OTP and store in Redis
        // - send email
        return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
    }

    /**
     * Forgot password
     * - Accept email or username
     * - Generate OTP for password reset
     * - Store in Redis (TTL = 5 minutes)
     * - Send OTP email
     */
    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword() {
        // TODO: send password reset OTP
        // - find user by email/username
        // - generate OTP, store in Redis
        // - send email
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
    ResponseEntity<?> resetPassword() {
        // TODO: reset password using OTP
        // - validate OTP
        // - encode and update password
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    /**
     * Change password (while logged in)
     * - Extract user from JWT
     * - Verify old password
     * - Encode and update new password
     */
    @PutMapping("/change-password")
    ResponseEntity<?> changePassword() {
        // TODO: change password flow
        // - validate old password
        // - encode and save new password
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

