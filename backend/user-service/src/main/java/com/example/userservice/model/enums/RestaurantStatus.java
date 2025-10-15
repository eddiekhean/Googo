package com.example.userservice.model.enums;

public enum RestaurantStatus {
    PENDING,     // Chờ duyệt (mới đăng ký)
    APPROVED,    // Đã được admin duyệt
    REJECTED,    // Bị từ chối
    BANNED,      // Bị khóa do vi phạm
    CLOSED;      // Tự đóng cửa / tạm ngưng
}
