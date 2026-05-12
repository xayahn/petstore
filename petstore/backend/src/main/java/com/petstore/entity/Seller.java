package com.petstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.petstore.entity.AuditedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seller JPA Entity
 * 
 * Represents a seller account in the marketplace.
 * Stores seller verification status, business information, and ratings.
 * 
 * Fields:
 * - userId: Foreign key to User entity (nullable for admin sellers)
 * - businessName: Official business name
 * - verificationStatus: PENDING, VERIFIED, REJECTED, SUSPENDED
 * - emailVerifiedAt: Timestamp when seller email was verified
 * - rating: Average seller rating (0.0 to 5.0)
 * - totalSales: Total number of sales/orders completed
 * - totalEarnings: Cumulative earnings from all sales
 * 
 * Relationships:
 * - User (OneToOne): The user account associated with this seller
 * - SellerProfile (OneToOne): Extended seller profile information
 * - Pets (OneToMany): All pet listings managed by this seller
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sellers", indexes = {
        @Index(name = "idx_seller_verification_status", columnList = "verification_status"),
        @Index(name = "idx_seller_business_name", columnList = "business_name"),
        @Index(name = "idx_seller_user_id", columnList = "user_id"),
        @Index(name = "idx_seller_rating", columnList = "rating")
})
public class Seller extends AuditedEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = true, length = 36)
    private String userId;

    @NotBlank(message = "Business name is required")
    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "email_verified_at", nullable = true)
    private LocalDateTime emailVerifiedAt;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @PositiveOrZero
    @Column(name = "total_sales", nullable = false)
    private Long totalSales = 0L;

    @DecimalMin("0.0")
    @Column(name = "total_earnings", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private SellerProfile profile;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    /**
     * Verification status enumeration for seller accounts
     */
    public enum VerificationStatus {
        PENDING, // Initial state, awaiting email verification
        VERIFIED, // Email verified, seller approved
        REJECTED, // Application rejected
        SUSPENDED // Temporarily or permanently suspended
    }

    /**
     * Check if seller is verified and active
     */
    public boolean isVerified() {
        return this.verificationStatus == VerificationStatus.VERIFIED;
    }

    /**
     * Check if seller email is verified
     */
    public boolean isEmailVerified() {
        return this.emailVerifiedAt != null;
    }
}
