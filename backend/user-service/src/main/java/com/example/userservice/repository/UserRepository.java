package com.example.userservice.repository;

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

}
