package com.example.userservice.model.entity;

import com.example.userservice.model.enums.AccountStatus;
import com.example.userservice.model.enums.Gender;
import com.example.userservice.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100)
    @Column(name = "full_name", length = 100)
    private String fullName;

    @Size(max = 15)
    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "date_of_birth", columnDefinition = "DATE DEFAULT '1900-01-01'")
    private LocalDate dateOfBirth = LocalDate.of(1900, 1, 1);

    @Size(max = 255)
    @Column(name = "avatar_url")
    private String avatarUrl;
    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "gender",
            columnDefinition = "gender_type"
    )
    private Gender gender = Gender.OTHER;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "user_role DEFAULT 'CUSTOMER'", nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "user_status DEFAULT 'INACTIVE'", nullable = false)
    private AccountStatus status = AccountStatus.INACTIVE;

}