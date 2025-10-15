package com.example.userservice.service.Imp;

import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.Gender;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Async("taskExecutor")
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
}