package com.petstore.repository;

import com.petstore.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SellerProfileRepository
 * 
 * Data access layer for SellerProfile entity.
 * Provides query methods for seller profile lookups.
 */
@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, String> {

    /**
     * Find profile by seller ID
     */
    Optional<SellerProfile> findBySellerId(String sellerId);

    /**
     * Check if seller has configured payout details
     */
    @Query("SELECT CASE WHEN (sp.bankAccountName IS NOT NULL AND sp.bankRoutingNumber IS NOT NULL AND sp.bankAccountNumber IS NOT NULL) THEN true ELSE false END FROM SellerProfile sp WHERE sp.seller.id = :sellerId")
    Boolean hasBankDetails(@Param("sellerId") String sellerId);
}
