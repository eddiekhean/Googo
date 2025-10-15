package com.example.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    // 🧩 USER MANAGEMENT
    @GetMapping("/users")
    ResponseEntity<?> getAllUsers() {
        // TODO: get list of all users with pagination & filters
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}")
    ResponseEntity<?> getUserById() {
        // TODO: get detailed user info by id
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/activate")
    ResponseEntity<?> activateUser() {
        // TODO: activate a user account
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/ban")
    ResponseEntity<?> banUser() {
        // TODO: ban or disable a user account
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<?> deleteUser() {
        // TODO: permanently delete a user
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 🧩 RESTAURANT MANAGEMENT
    @GetMapping("/restaurants")
    ResponseEntity<?> getAllRestaurants() {
        // TODO: get all restaurants with status and owner info
        return ResponseEntity.ok().build();
    }

    @GetMapping("/restaurants/{id}")
    ResponseEntity<?> getRestaurantById() {
        // TODO: get detailed restaurant info
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/restaurants/{id}/approve")
    ResponseEntity<?> approveRestaurant() {
        // TODO: approve restaurant registration
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/restaurants/{id}/ban")
    ResponseEntity<?> banRestaurant() {
        // TODO: ban a restaurant account
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/restaurants/{id}")
    ResponseEntity<?> deleteRestaurant() {
        // TODO: permanently delete restaurant and its linked user
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 🧩 DASHBOARD / STATISTICS
    @GetMapping("/statistics")
    ResponseEntity<?> getSystemStatistics() {
        // TODO: get total users, restaurants, active accounts, etc.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs")
    ResponseEntity<?> getSystemLogs() {
        // TODO: get admin activity logs or audit trail
        return ResponseEntity.ok().build();
    }

    @PostMapping("/broadcast")
    ResponseEntity<?> sendSystemAnnouncement() {
        // TODO: broadcast message or notification to all users
        return ResponseEntity.ok().build();
    }
}