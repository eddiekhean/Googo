package com.example.userservice.service.Imp;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.ProfileService;
import com.example.userservice.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    @Override
    public UserResponse getUserProfile() {
        String userId = currentUserProvider.getStringUserName();
        return userRepository.findUserWithDefaultAddress(UUID.fromString(userId)).stream().findFirst()
                .orElseThrow();
    }
}
