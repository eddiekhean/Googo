package com.example.userservice.repository;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.model.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getUsersByUsername(@Size(max = 50) @NotNull String username);
    Boolean existsByUsername(@Size(max = 50) @NotNull String username);
    Boolean existsByEmail(@Size(max = 50) @NotNull String email);
    Boolean existsByPhone(@Size(max = 50) @NotNull String phone);

    @Query("SELECT u FROM User u WHERE (u.username = :identifier OR u.email = :identifier) ")
    Optional<User> findLoginUser(@Param("identifier") String identifier);

    @Query("SELECT u.email FROM User u WHERE u.username = :username")
    String getEmailByUsername(String username);

    Optional<User> getUsersByEmail(String identifier);

    @Query("""
      SELECT new com.example.userservice.dto.UserResponse(
          u.username,
          u.email,
          u.fullName,
          u.phone,
          u.dateOfBirth,
          u.avatarUrl,
          u.createdAt,
          u.updatedAt,
          u.gender,
          u.status,
          a.street
      )
      FROM User u
      LEFT JOIN Address a
        ON a.user = u AND a.isDefault = true
      WHERE u.id = :userId
""")
    Optional<UserResponse> findUserWithDefaultAddress(UUID userId);


    Optional<User> getUsersById(UUID id);
}
