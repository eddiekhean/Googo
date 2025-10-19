package com.example.userservice.service.Imp;

import com.example.userservice.dto.*;
import com.example.userservice.event.producer.MailEventProducer;
import com.example.userservice.exception.InvalidOtpException;
import com.example.userservice.exception.InvalidRefreshTokenException;
import com.example.userservice.exception.OtpExpiredException;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.Gender;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import com.example.userservice.util.CurrentUserProvider;
import com.example.userservice.util.JwtUtil;
import com.example.userservice.util.OtpUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MailEventProducer mailEventProducer;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CurrentUserProvider currentUserProvider;
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            String key = "refresh_token::" + refreshToken;
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));

            return new AuthResponse(accessToken, refreshToken, jwtUtil.getAccessExpiration());

        } catch (BadCredentialsException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void register(RegisterRequest request) throws BadRequestException {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setFullName(request.getFullName());
            user.setDateOfBirth(
                    request.getDateOfBirth() != null
                            ? LocalDate.parse(request.getDateOfBirth())
                            : null
            );
            user.setGender(Gender.OTHER);
            //TODO sending mail welcome
            userRepository.save(user);
        } catch (UserAlreadyExistsException e) {
            throw new BadRequestException("Duplicate user info");
        }

    }

    @Override
    public void sendOtp() {
        String otp = OtpUtil.generateOtp(6);
        String email = userRepository.getEmailByUsername(currentUserProvider.getStringUserName());
        String userId = currentUserProvider.getUserId().toString();
        // Tạo 1 map chứa thông tin OTP + userId

        Map<String, String> otpData = Map.of(
                "userId", userId,
                "otp", otp
        );

        String key = "otp:" + userId;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForHash().putAll(key, otpData);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
        // Tạo mail event
        MailEvent event = MailEvent.builder()
                .type("OTP")
                .to(email)
                .data(Map.of("otp", otp))
                .build();

        mailEventProducer.sendMailEvent(event);
    }

    @Override
    public boolean verifyOtp(String otp) {
        String key = "otp:" + currentUserProvider.getUserId().toString();
        Object storedOtp = redisTemplate.opsForHash().get(key, "otp");

        if (storedOtp == null) {
            throw new OtpExpiredException("OTP đã hết hạn hoặc không tồn tại");
        }

        if (!storedOtp.equals(otp)) {
            throw new InvalidOtpException("OTP không hợp lệ");
        }
        redisTemplate.delete(key);
        return false;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }

        Claims claims = jwtUtil.extractClaims(refreshToken);
        String userId = claims.getSubject();

        // So khớp Redis
        String key = "refresh_token::" + userId;
        String storedToken = redisTemplate.opsForValue().get(key).toString();

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token revoked or does not match");
        }

        // Nếu qua hết → sinh access token mới
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));

        return new AuthResponse(newAccessToken, newRefreshToken, jwtUtil.getAccessExpiration());
    }

    @Override
    public void logout(String token, String refreshToken) {
        String key = "refresh_token::"+refreshToken;
        Claims claims = jwtUtil.extractClaims(token);
        Date expiration = claims.getExpiration();
        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        String bannerKey = "banner_token::" + token;
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(
                    bannerKey,
                    token,
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        } else {
            throw new IllegalStateException("Token đã hết hạn");
        }
        redisTemplate.delete(key);
    }

    @Override
    public void forgotPasswordUserName(String identifier) {
        User user = userRepository.getUsersByUsername(identifier).stream().findFirst().orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy user với username: " + identifier));

        String otp = OtpUtil.generateOtp(6);
        Map<String, String> otpData = Map.of(
                "userId", user.getId().toString(),
                "otp", otp
        );

        String key = "otp:" + user.getId().toString();
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForHash().putAll(key, otpData);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
        // Tạo mail event
        MailEvent event = MailEvent.builder()
                .type("OTP")
                .to(user.getEmail())
                .data(Map.of("otp", otp))
                .build();

        mailEventProducer.sendMailEvent(event);
    }
    @Override
    public void forgotPasswordEmail(String identifier) {
        User user = userRepository.getUsersByEmail(identifier).stream().findFirst().orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy user với username: " + identifier));

        String otp = OtpUtil.generateOtp(6);
        Map<String, String> otpData = Map.of(
                "userId", user.getId().toString(),
                "otp", otp
        );

        String key = "otp:" + user.getId().toString();
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        redisTemplate.opsForHash().putAll(key, otpData);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
        // Tạo mail event
        MailEvent event = MailEvent.builder()
                .type("OTP")
                .to(user.getEmail())
                .data(Map.of("otp", otp))
                .build();

        mailEventProducer.sendMailEvent(event);
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.getUsersByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String key = "otp:" + user.getId();
        Object storedOtp = redisTemplate.opsForHash().get(key, "otp");

        if (storedOtp == null) {
            throw new OtpExpiredException("OTP expired or does not exist");
        }
        if (!storedOtp.equals(otp)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(key); // cleanup OTP
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        String userId = currentUserProvider.getUserId().toString();
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}