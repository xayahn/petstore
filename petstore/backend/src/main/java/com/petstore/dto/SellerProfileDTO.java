package com.petstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SellerProfile Data Transfer Object
 * 
 * Extended seller information for API responses.
 * Includes profile-specific fields like bio, return policy, payout settings.
 * Bank account numbers are masked in responses.
 */
@Data
@NoArgsConstructor
public class SellerProfileDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("seller_id")
    private String sellerId;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("return_policy")
    private String returnPolicy;

    @JsonProperty("bank_account_name")
    private String bankAccountName;

    @JsonProperty("bank_routing_number_masked")
    private String bankRoutingNumberMasked;

    @JsonProperty("bank_account_number_masked")
    private String bankAccountNumberMasked;

    @JsonProperty("payout_frequency")
    private String payoutFrequency;

    @JsonProperty("minimum_payout_amount")
    private BigDecimal minimumPayoutAmount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Mask sensitive bank account information
     * Leaves first 2 and last 4 digits visible
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 6) {
            return "****";
        }
        return accountNumber.substring(0, 2) +
                "*".repeat(Math.max(0, accountNumber.length() - 6)) +
                accountNumber.substring(accountNumber.length() - 4);
    }
}
