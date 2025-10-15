package com.example.userservice.model.entity;

import com.example.userservice.model.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurant_profiles")
public class RestaurantProfile {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Liên kết 1-1 với bảng users (mỗi user chỉ có 1 profile restaurant)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Liên kết đến bảng addresses (có thể null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "opening_hours", length = 100)
    private String openingHours;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_open", nullable = false)
    @ColumnDefault("true")
    private Boolean isOpen = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "restaurant_status DEFAULT 'PENDING'")
    private RestaurantStatus status = RestaurantStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    @ColumnDefault("now()")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @ColumnDefault("now()")
    private Instant updatedAt = Instant.now();
}