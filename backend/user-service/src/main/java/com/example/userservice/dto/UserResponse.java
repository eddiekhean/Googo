package com.example.userservice.dto;

import com.example.userservice.model.enums.AccountStatus;
import com.example.userservice.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private Gender gender;
    private AccountStatus status;
    private String defaultAddress;
}