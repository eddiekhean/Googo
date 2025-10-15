package com.example.userservice.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {


    // ✅ Lấy thông tin cá nhân
    @GetMapping("/me")
    ResponseEntity<?> getProfile() {
        // TODO: get user info by access token
        // - Extract userId from JWT
        // - Query user profile from DB (User table)
        // - Exclude password, tokens, and internal fields
        // - Return as UserDto
        return ResponseEntity.ok().build();
    }

    // ✅ Cập nhật thông tin cá nhân
    @PutMapping("/me")
    ResponseEntity<?> updateProfile() {
        // TODO: update basic user info (full name, phone, gender, dob)
        // - Validate input
        // - Map request -> entity
        // - Save via userService
        // - Return updated profile
        return ResponseEntity.ok().build();
    }

    // ✅ Upload avatar
    @PostMapping("/me/avatar")
    ResponseEntity<?> uploadAvatar() {
        // TODO: upload avatar to Cloudinary or S3
        // - Validate file format (jpg/png/webp)
        // - userService.uploadAvatar(userId, file)
        // - Return avatar URL
        return ResponseEntity.ok().build();
    }

    // ✅ Lấy tất cả địa chỉ của user
    @GetMapping("/me/addresses")
    ResponseEntity<?> getAddresses() {
        // TODO: get all addresses linked to current user
        // - Query addresses by user_id
        // - Return List<AddressDto>
        return ResponseEntity.ok().build();
    }

    // ✅ Thêm địa chỉ mới
    @PostMapping("/me/addresses")
    ResponseEntity<?> addAddress() {
        // TODO: add new address for user
        // - Validate address fields
        // - If isDefault = true, reset all others to false
        // - Insert record, return AddressDto
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ Cập nhật địa chỉ
    @PatchMapping("/me/addresses/{id}")
    ResponseEntity<?> updateAddress() {
        // TODO: update address if it belongs to user
        // - Verify ownership
        // - Update selected fields
        // - Return updated AddressDto
        return ResponseEntity.ok().build();
    }

    // ✅ Xóa địa chỉ
    @DeleteMapping("/me/addresses/{id}")
    ResponseEntity<?> deleteAddress() {
        // TODO: soft/hard delete address
        // - Ensure address belongs to current user
        // - Delete from DB
        // - Return success message
        return ResponseEntity.ok().build();
    }

    // ✅ Đặt địa chỉ mặc định
    @PatchMapping("/me/addresses/{id}/set-default")
    ResponseEntity<?> setDefaultAddress() {
        // TODO: mark address as default
        // - Reset all others to false
        // - Set this one to true
        // - Return confirmation
        return ResponseEntity.ok().build();
    }

    // ✅ Đổi email
    @PatchMapping("/me/change-email")
    ResponseEntity<?> changeEmail(@RequestParam("newEmail") String newEmail) {
        // TODO: handle email change flow
        // - Send OTP to new email
        // - Require verification before commit
        // - Update email after confirmation
        return ResponseEntity.ok().build();
    }

    // ✅ Kích hoạt lại tài khoản
    @PatchMapping("/me/reactivate")
    ResponseEntity<?> reactivateAccount() {
        // TODO: allow reactivation for INACTIVE users
        // - Update user.status = ACTIVE
        // - Send notification email
        // - Return confirmation
        return ResponseEntity.ok().build();
    }

    // ✅ Khóa tài khoản (deactivate)
    @PatchMapping("/me/deactivate")
    ResponseEntity<?> deactivateAccount() {
        // TODO: soft-deactivate account
        // - Set user.status = INACTIVE
        // - Revoke tokens
        // - Return success message
        return ResponseEntity.ok().build();
    }

    // ✅ Xóa tài khoản vĩnh viễn
    @DeleteMapping("/me")
    ResponseEntity<?> deleteAccount() {
        // TODO: hard delete user account
        // - Remove all linked data (address, avatar, etc.)
        // - Optionally anonymize user info
        // - Return success message
        return ResponseEntity.ok().build();
    }

    // ✅ Cập nhật cài đặt thông báo (notification settings)
    @PatchMapping("/me/settings/notifications")
    ResponseEntity<?> updateNotificationSettings() {
        // TODO: update email/push notification preferences
        // - Save to user_settings table
        // - Return current settings
        return ResponseEntity.ok().build();
    }

    // ✅ Xem lịch sử hoạt động (optional)
    @GetMapping("/me/activity")
    ResponseEntity<?> getActivityLogs() {
        // TODO: fetch recent user actions
        // - Query from user_activity table (or log service)
        // - Paginate results
        // - Return list of activities
        return ResponseEntity.ok().build();
    }
}
