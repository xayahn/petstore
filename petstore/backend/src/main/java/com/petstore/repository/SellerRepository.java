package com.petstore.repository;

import com.petstore.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * SellerRepository
 * 
 * Data access layer for Seller entity.
 * Provides query methods for seller searches, filtering, and statistics.
 */
@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {

    /**
     * Find seller by user ID
     */
    Optional<Seller> findByUserId(String userId);

    /**
     * Find seller by business name
     */
    Optional<Seller> findByBusinessName(String businessName);

    /**
     * Find all sellers with specific verification status (paginated)
     */
    Page<Seller> findByVerificationStatus(Seller.VerificationStatus status, Pageable pageable);

    /**
     * Find verified and active sellers with minimum rating
     */
    @Query("SELECT s FROM Seller s WHERE s.verificationStatus = 'VERIFIED' AND s.rating >= :minRating ORDER BY s.rating DESC")
    Page<Seller> findActiveSellersByRating(@Param("minRating") BigDecimal minRating, Pageable pageable);

    /**
     * Find verified sellers with profiles (optimized for public profile display)
     */
    @Query("SELECT s FROM Seller s LEFT JOIN FETCH s.profile WHERE s.verificationStatus = 'VERIFIED'")
    Page<Seller> findVerifiedSellersWithProfiles(Pageable pageable);

    /**
     * Find sellers by business name (search)
     */
    @Query("SELECT s FROM Seller s WHERE LOWER(s.businessName) LIKE LOWER(CONCAT('%', :query, '%')) AND s.verificationStatus = 'VERIFIED'")
    Page<Seller> searchByBusinessName(@Param("query") String query, Pageable pageable);

    /**
     * Count sellers by verification status
     */
    Long countByVerificationStatus(Seller.VerificationStatus status);

    /**
     * Check if seller email is verified
     */
    @Query("SELECT CASE WHEN s.emailVerifiedAt IS NOT NULL THEN true ELSE false END FROM Seller s WHERE s.id = :sellerId")
    Boolean isEmailVerified(@Param("sellerId") String sellerId);
}
