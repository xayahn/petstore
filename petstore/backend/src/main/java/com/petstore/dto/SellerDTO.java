package com.petstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seller Data Transfer Object
 * 
 * Used for detailed seller information in API responses.
 * Includes all seller fields plus profile information.
 */
@Data
@NoArgsConstructor
public class SellerDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("verification_status")
    private String verificationStatus;

    @JsonProperty("email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @JsonProperty("rating")
    private BigDecimal rating;

    @JsonProperty("total_sales")
    private Long totalSales;

    @JsonProperty("total_earnings")
    private BigDecimal totalEarnings;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("profile")
    private SellerProfileDTO profile;

    /**
     * Helper method to check if seller is verified
     */
    public boolean isVerified() {
        return "VERIFIED".equals(verificationStatus);
    }
}
