package com.petstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.petstore.entity.AuditedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

/**
 * SellerProfile JPA Entity
 * 
 * Extended seller information beyond basic business details.
 * One-to-One relationship with Seller entity.
 * 
 * Fields:
 * - sellerId: Foreign key to Seller (required, unique)
 * - bio: Seller biography/description (optional)
 * - returnPolicy: Return policy text (optional)
 * - bankAccountName: Account holder name for payouts
 * - bankRoutingNumber: Bank routing number (masked in responses)
 * - bankAccountNumber: Bank account number (masked in responses)
 * - payoutFrequency: How often seller receives payouts (WEEKLY, MONTHLY,
 * ON_DEMAND)
 * - minimumPayoutAmount: Minimum threshold before payout is processed
 * 
 * Relationships:
 * - Seller (OneToOne): The associated seller account
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "seller_profiles", indexes = {
        @Index(name = "idx_seller_profile_seller_id", columnList = "seller_id", unique = true)
})
public class SellerProfile extends AuditedEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private Seller seller;

    @Column(name = "bio", nullable = true, columnDefinition = "TEXT")
    private String bio;

    @Column(name = "return_policy", nullable = true, columnDefinition = "TEXT")
    private String returnPolicy;

    @Column(name = "bank_account_name", nullable = true, length = 100)
    private String bankAccountName;

    @Column(name = "bank_routing_number", nullable = true, length = 20)
    private String bankRoutingNumber;

    @Column(name = "bank_account_number", nullable = true, length = 20)
    private String bankAccountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_frequency", nullable = false, length = 20)
    private PayoutFrequency payoutFrequency = PayoutFrequency.MONTHLY;

    @DecimalMin("10.00")
    @Column(name = "minimum_payout_amount", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal minimumPayoutAmount = new java.math.BigDecimal("100.00");

    /**
     * Payout frequency enumeration
     */
    public enum PayoutFrequency {
        WEEKLY,
        MONTHLY,
        ON_DEMAND,
        QUARTERLY
    }

    /**
     * Check if bank account details are configured
     */
    public boolean hasBankDetails() {
        return bankAccountName != null &&
                bankRoutingNumber != null &&
                bankAccountNumber != null;
    }
}
