package com.petstore.service;

import com.petstore.entity.Seller;
import com.petstore.entity.SellerProfile;
import com.petstore.dto.SellerDTO;
import com.petstore.dto.SellerProfileDTO;
import com.petstore.repository.SellerRepository;
import com.petstore.repository.SellerProfileRepository;
import layug.exception.NotFoundException;
import layug.exception.ValidationException;
import com.petstore.service.ServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * SellerServiceTest
 * 
 * Unit tests for SellerService.
 * Tests seller registration, verification, profile management, and lookups.
 */
public class SellerServiceTest extends ServiceTestBase {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private SellerService sellerService;

    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId("seller-123");
        testSeller.setUserId("user-123");
        testSeller.setBusinessName("Test Pet Shop");
        testSeller.setVerificationStatus(Seller.VerificationStatus.PENDING);
        testSeller.setRating(new BigDecimal("4.50"));
        testSeller.setTotalSales(50L);
    }

    /**
     * T108a - Test successful seller registration
     */
    @Test
    void testRegisterSeller() {
        when(sellerRepository.findByUserId("user-123")).thenReturn(Optional.empty());
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerDTO result = sellerService.registerSeller("user-123", "Test Pet Shop");

        assertThat(result, notNullValue());
        assertThat(result.getBusinessName(), equalTo("Test Pet Shop"));
        assertThat(result.getVerificationStatus(), equalTo("PENDING"));

        verify(sellerRepository, times(1)).findByUserId("user-123");
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    /**
     * T108b - Test registration fails for empty business name
     */
    @Test
    void testRegisterSellerWithEmptyBusinessName() {
        when(sellerRepository.findByUserId("user-123")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> {
            sellerService.registerSeller("user-123", "");
        });
    }

    /**
     * T108c - Test registration fails if seller already exists
     */
    @Test
    void testRegisterSellerAlreadyExists() {
        when(sellerRepository.findByUserId("user-123")).thenReturn(Optional.of(testSeller));

        assertThrows(ValidationException.class, () -> {
            sellerService.registerSeller("user-123", "Another Shop");
        });
    }

    /**
     * T108d - Test successful email verification
     */
    @Test
    void testVerifySeller() {
        String token = "verification-token-123";

        when(emailVerificationService.verifyToken(token)).thenReturn("seller-123");
        when(sellerRepository.findById("seller-123")).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller seller = invocation.getArgument(0);
            seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
            return seller;
        });

        SellerDTO result = sellerService.verifySeller(token);

        assertThat(result, notNullValue());
        assertThat(result.getVerificationStatus(), equalTo("VERIFIED"));
        assertThat(result.getEmailVerifiedAt(), notNullValue());

        verify(emailVerificationService, times(1)).verifyToken(token);
        verify(sellerRepository, times(1)).findById("seller-123");
    }

    /**
     * T108e - Test verification fails with invalid token
     */
    @Test
    void testVerifySellerInvalidToken() {
        String invalidToken = "invalid-token";

        when(emailVerificationService.verifyToken(invalidToken)).thenReturn(null);

        assertThrows(ValidationException.class, () -> {
            sellerService.verifySeller(invalidToken);
        });
    }

    /**
     * T108f - Test get seller profile
     */
    @Test
    void testGetSellerProfile() {
        SellerProfile profile = new SellerProfile();
        profile.setId("profile-123");
        profile.setBio("Professional breeder");
        testSeller.setProfile(profile);

        when(sellerRepository.findById("seller-123")).thenReturn(Optional.of(testSeller));

        SellerDTO result = sellerService.getSellerProfile("seller-123");

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo("seller-123"));
        assertThat(result.getProfile(), notNullValue());
        assertThat(result.getProfile().getBio(), equalTo("Professional breeder"));
    }

    /**
     * T108g - Test get seller profile not found
     */
    @Test
    void testGetSellerProfileNotFound() {
        when(sellerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            sellerService.getSellerProfile("nonexistent");
        });
    }

    /**
     * T108h - Test update seller profile
     */
    @Test
    void testUpdateSellerProfile() {
        SellerProfile profile = new SellerProfile();
        profile.setSeller(testSeller);
        testSeller.setProfile(profile);

        when(sellerRepository.findById("seller-123")).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerProfileDTO updateDto = new SellerProfileDTO();
        updateDto.setBio("Updated bio");
        updateDto.setReturnPolicy("30 day returns");
        updateDto.setPayoutFrequency("WEEKLY");

        SellerDTO result = sellerService.updateSellerProfile("seller-123", updateDto);

        assertThat(result, notNullValue());
        verify(sellerRepository, times(1)).findById("seller-123");
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    /**
     * T108i - Test get verified sellers with minimum rating
     */
    @Test
    void testGetVerifiedSellers() {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);

        Page<Seller> page = new PageImpl<>(sellers);
        Pageable pageable = PageRequest.of(0, 10);

        when(sellerRepository.findActiveSellersByRating(new BigDecimal("4.0"), pageable))
                .thenReturn(page);

        Page<SellerDTO> result = sellerService.getVerifiedSellers(new BigDecimal("4.0"), pageable);

        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getBusinessName(), equalTo("Test Pet Shop"));
    }

    /**
     * T108j - Test search sellers by business name
     */
    @Test
    void testSearchSellers() {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);

        Page<Seller> page = new PageImpl<>(sellers);
        Pageable pageable = PageRequest.of(0, 10);

        when(sellerRepository.searchByBusinessName("Test", pageable)).thenReturn(page);

        Page<SellerDTO> result = sellerService.searchSellers("Test", pageable);

        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getBusinessName(), containsString("Test"));
    }

    /**
     * T108k - Test get sellers by verification status
     */
    @Test
    void testGetSellersByStatus() {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);

        Page<Seller> page = new PageImpl<>(sellers);
        Pageable pageable = PageRequest.of(0, 10);

        when(sellerRepository.findByVerificationStatus(Seller.VerificationStatus.PENDING, pageable))
                .thenReturn(page);

        Page<SellerDTO> result = sellerService.getSellersByStatus(Seller.VerificationStatus.PENDING, pageable);

        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getVerificationStatus(), equalTo("PENDING"));
    }

    /**
     * T108l - Test profile DTO has masked bank account numbers
     */
    @Test
    void testProfileDTOMasksAccountNumbers() {
        SellerProfile profile = new SellerProfile();
        profile.setId("profile-123");
        profile.setBankAccountName("John Smith");
        profile.setBankRoutingNumber("123456789");
        profile.setBankAccountNumber("9876543210");
        profile.setSeller(testSeller);
        testSeller.setProfile(profile);

        when(sellerRepository.findById("seller-123")).thenReturn(Optional.of(testSeller));

        SellerDTO result = sellerService.getSellerProfile("seller-123");

        assertThat(result.getProfile(), notNullValue());
        // Check that account numbers are masked
        assertThat(result.getProfile().getBankAccountNumberMasked(), notNullValue());
        assertThat(result.getProfile().getBankAccountNumberMasked(),
                not(containsString("9876543210")));
    }
}
