package com.petstore.service;

import com.petstore.entity.Seller;
import com.petstore.entity.SellerProfile;
import com.petstore.dto.SellerDTO;
import com.petstore.dto.SellerProfileDTO;
import com.petstore.repository.SellerRepository;
import com.petstore.repository.SellerProfileRepository;
import layug.exception.NotFoundException;
import layug.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SellerService
 * 
 * Business logic for seller account management.
 * Handles seller registration, verification, profile management, and lookups.
 */
@Slf4j
@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    /**
     * Register new seller account
     * Creates seller with PENDING verification status
     */
    @Transactional
    public SellerDTO registerSeller(String userId, String businessName) {
        log.info("Registering seller: userId={}, businessName={}", userId, businessName);

        // Validate input
        if (businessName == null || businessName.trim().isEmpty()) {
            throw new ValidationException("Business name is required");
        }

        // Check if seller already exists for this user
        if (sellerRepository.findByUserId(userId).isPresent()) {
            throw new ValidationException("User already has a seller account");
        }

        // Create seller
        Seller seller = new Seller();
        seller.setUserId(userId);
        seller.setBusinessName(businessName.trim());
        seller.setVerificationStatus(Seller.VerificationStatus.PENDING);

        // Create default profile
        SellerProfile profile = new SellerProfile();
        profile.setSeller(seller);
        seller.setProfile(profile);

        Seller saved = sellerRepository.save(seller);

        log.info("Seller registered successfully: sellerId={}", saved.getId());
        return convertToDTO(saved);
    }

    /**
     * Verify seller email with token
     * Sets verification status to VERIFIED after successful verification
     */
    @Transactional
    public SellerDTO verifySeller(String token) {
        log.info("Verifying seller with token");

        String sellerId = emailVerificationService.verifyToken(token);
        if (sellerId == null) {
            throw new ValidationException("Invalid or expired verification token");
        }

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        seller.setEmailVerifiedAt(LocalDateTime.now());
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);

        Seller updated = sellerRepository.save(seller);
        log.info("Seller verified successfully: sellerId={}", sellerId);

        return convertToDTO(updated);
    }

    /**
     * Get seller profile by ID
     */
    @Transactional(readOnly = true)
    public SellerDTO getSellerProfile(String sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found: " + sellerId));

        return convertToDTO(seller);
    }

    /**
     * Get seller by User ID
     */
    @Transactional(readOnly = true)
    public Seller getSellerByUserId(String userId) {
        return sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Seller not found for user: " + userId));
    }

    /**
     * Update seller profile information
     */
    @Transactional
    public SellerDTO updateSellerProfile(String sellerId, SellerProfileDTO profileDto) {
        log.info("Updating seller profile: sellerId={}", sellerId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        SellerProfile profile = seller.getProfile();
        if (profile == null) {
            profile = new SellerProfile();
            profile.setSeller(seller);
        }

        // Update profile fields
        if (profileDto.getBio() != null) {
            profile.setBio(profileDto.getBio());
        }
        if (profileDto.getReturnPolicy() != null) {
            profile.setReturnPolicy(profileDto.getReturnPolicy());
        }
        if (profileDto.getPayoutFrequency() != null) {
            try {
                profile.setPayoutFrequency(
                        SellerProfile.PayoutFrequency.valueOf(profileDto.getPayoutFrequency()));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid payout frequency");
            }
        }
        if (profileDto.getMinimumPayoutAmount() != null) {
            if (profileDto.getMinimumPayoutAmount().compareTo(new BigDecimal("10.00")) < 0) {
                throw new ValidationException("Minimum payout amount must be at least $10.00");
            }
            profile.setMinimumPayoutAmount(profileDto.getMinimumPayoutAmount());
        }

        seller.setProfile(profile);
        Seller updated = sellerRepository.save(seller);

        log.info("Seller profile updated: sellerId={}", sellerId);
        return convertToDTO(updated);
    }

    /**
     * Get paginated list of verified sellers with minimum rating
     */
    @Transactional(readOnly = true)
    public Page<SellerDTO> getVerifiedSellers(BigDecimal minRating, Pageable pageable) {
        Page<Seller> sellers = sellerRepository.findActiveSellersByRating(minRating, pageable);
        return sellers.map(this::convertToDTO);
    }

    /**
     * Search sellers by business name
     */
    @Transactional(readOnly = true)
    public Page<SellerDTO> searchSellers(String query, Pageable pageable) {
        Page<Seller> sellers = sellerRepository.searchByBusinessName(query, pageable);
        return sellers.map(this::convertToDTO);
    }

    /**
     * Get sellers by verification status
     */
    @Transactional(readOnly = true)
    public Page<SellerDTO> getSellersByStatus(Seller.VerificationStatus status, Pageable pageable) {
        Page<Seller> sellers = sellerRepository.findByVerificationStatus(status, pageable);
        return sellers.map(this::convertToDTO);
    }

    /**
     * Convert Seller entity to DTO
     */
    private SellerDTO convertToDTO(Seller seller) {
        SellerDTO dto = new SellerDTO();
        dto.setId(seller.getId());
        dto.setUserId(seller.getUserId());
        dto.setBusinessName(seller.getBusinessName());
        dto.setVerificationStatus(seller.getVerificationStatus().toString());
        dto.setEmailVerifiedAt(seller.getEmailVerifiedAt());
        dto.setRating(seller.getRating());
        dto.setTotalSales(seller.getTotalSales());
        dto.setTotalEarnings(seller.getTotalEarnings());
        dto.setCreatedAt(seller.getCreatedAt());
        dto.setUpdatedAt(seller.getUpdatedAt());

        if (seller.getProfile() != null) {
            dto.setProfile(convertProfileToDTO(seller.getProfile()));
        }

        return dto;
    }

    /**
     * Convert SellerProfile entity to DTO
     */
    private SellerProfileDTO convertProfileToDTO(SellerProfile profile) {
        SellerProfileDTO dto = new SellerProfileDTO();
        dto.setId(profile.getId());
        dto.setSellerId(profile.getSeller().getId());
        dto.setBio(profile.getBio());
        dto.setReturnPolicy(profile.getReturnPolicy());
        dto.setBankAccountName(profile.getBankAccountName());

        // Mask sensitive data
        if (profile.getBankRoutingNumber() != null) {
            dto.setBankRoutingNumberMasked(SellerProfileDTO.maskAccountNumber(profile.getBankRoutingNumber()));
        }
        if (profile.getBankAccountNumber() != null) {
            dto.setBankAccountNumberMasked(SellerProfileDTO.maskAccountNumber(profile.getBankAccountNumber()));
        }

        dto.setPayoutFrequency(profile.getPayoutFrequency().toString());
        dto.setMinimumPayoutAmount(profile.getMinimumPayoutAmount());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());

        return dto;
    }
}
