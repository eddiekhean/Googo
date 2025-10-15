package com.example.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@PreAuthorize("hasRole('RESTAURANT')") // 🔒 only restaurant owners can access
public class RestaurantController {

    @GetMapping("/me")
    ResponseEntity<?> getMyProfile() {
        // TODO: return restaurant profile (name, address, contact, is_open, description, etc.)
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    ResponseEntity<?> updateProfile() {
        // TODO: update restaurant info (name, description, address, opening hours, image_url, etc.)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/toggle-open")
    ResponseEntity<?> toggleOpenStatus() {
        // TODO: toggle restaurant open/close status
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews")
    ResponseEntity<?> getRestaurantReviews() {
        // TODO: get list of reviews for current restaurant (average rating, total reviews)
        return ResponseEntity.ok().build();
    }

    // ==========================
    // 🍔 PRODUCT / MENU MANAGEMENT
    // ==========================

    @GetMapping("/products")
    ResponseEntity<?> getAllProducts() {
        // TODO: list all products (foods/drinks) owned by restaurant
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products")
    ResponseEntity<?> createProduct() {
        // TODO: create a new product (name, price, category, description, images)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/products/{id}")
    ResponseEntity<?> updateProduct() {
        // TODO: update product info (price, stock, image, etc.)
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{id}")
    ResponseEntity<?> deleteProduct() {
        // TODO: soft delete a product
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/products/{id}/status")
    ResponseEntity<?> updateProductStatus() {
        // TODO: enable/disable product (for hiding unavailable items)
        return ResponseEntity.ok().build();
    }

    // ==========================
    // 📦 ORDER MANAGEMENT
    // ==========================

    @GetMapping("/orders")
    ResponseEntity<?> getAllOrders() {
        // TODO: get all orders related to this restaurant (filter by status)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/{id}")
    ResponseEntity<?> getOrderDetail() {
        // TODO: get specific order detail (items, customer, delivery info)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{id}/accept")
    ResponseEntity<?> acceptOrder() {
        // TODO: mark order as accepted
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{id}/reject")
    ResponseEntity<?> rejectOrder() {
        // TODO: reject order (with reason)
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{id}/complete")
    ResponseEntity<?> completeOrder() {
        // TODO: mark order as completed/delivered
        return ResponseEntity.ok().build();
    }

    // ==========================
    // 📊 ANALYTICS & DASHBOARD
    // ==========================

    @GetMapping("/dashboard/statistics")
    ResponseEntity<?> getDashboardStats() {
        // TODO: get total orders, revenue, best-selling products, etc.
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/top-products")
    ResponseEntity<?> getTopProducts() {
        // TODO: return top 5 selling products
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/revenue")
    ResponseEntity<?> getRevenueChart() {
        // TODO: return daily/weekly/monthly revenue analytics
        return ResponseEntity.ok().build();
    }

    // ==========================
    // 🧾 SUPPORT / SETTINGS
    // ==========================

    @PostMapping("/support")
    ResponseEntity<?> contactSupport() {
        // TODO: send a message to admin (report issue, request help)
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deactivate")
    ResponseEntity<?> deactivateAccount() {
        // TODO: deactivate restaurant account (set status = INACTIVE)
        return ResponseEntity.ok().build();
    }
}

