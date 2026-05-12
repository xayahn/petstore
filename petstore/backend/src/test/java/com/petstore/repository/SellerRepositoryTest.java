package com.petstore.repository;

import com.petstore.entity.Seller;
import com.petstore.entity.SellerProfile;
import com.petstore.test.IntegrationTestBase;
import com.petstore.test.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SellerRepositoryTest
 * 
 * Integration tests for SellerRepository and SellerProfileRepository.
 * Tests all custom query methods with real PostgreSQL database
 * (Testcontainers).
 */
public class SellerRepositoryTest extends IntegrationTestBase {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setUp() {
        databaseSetup.truncateAllTables();
    }

    /**
     * T105a - Test saving and retrieving seller
     */
    @Test
    void testSaveAndRetrieveSeller() {
        Seller seller = new Seller();
        seller.setBusinessName("Pet Paradise");
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
        seller.setRating(new BigDecimal("4.50"));
        seller.setTotalSales(100L);

        Seller saved = sellerRepository.save(seller);

        assertThat(saved.getId(), notNullValue());

        Seller retrieved = sellerRepository.findById(saved.getId()).orElse(null);
        assertThat(retrieved, notNullValue());
        assertThat(retrieved.getBusinessName(), equalTo("Pet Paradise"));
        assertThat(retrieved.getRating(), comparesEqualTo(new BigDecimal("4.50")));
    }

    /**
     * T105b - Test finding seller by user ID
     */
    @Test
    void testFindByUserId() {
        Seller seller = new Seller();
        seller.setUserId("user-123");
        seller.setBusinessName("Happy Pets");
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);

        sellerRepository.save(seller);

        Seller found = sellerRepository.findByUserId("user-123").orElse(null);
        assertThat(found, notNullValue());
        assertThat(found.getBusinessName(), equalTo("Happy Pets"));
    }

    /**
     * T105c - Test finding by verification status
     */
    @Test
    void testFindByVerificationStatus() {
        // Create verified seller
        Seller verified = new Seller();
        verified.setBusinessName("Verified Seller");
        verified.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
        sellerRepository.save(verified);

        // Create pending seller
        Seller pending = new Seller();
        pending.setBusinessName("Pending Seller");
        pending.setVerificationStatus(Seller.VerificationStatus.PENDING);
        sellerRepository.save(pending);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seller> verifiedSellers = sellerRepository.findByVerificationStatus(
                Seller.VerificationStatus.VERIFIED,
                pageable);

        assertThat(verifiedSellers.getContent(), hasSize(1));
        assertThat(verifiedSellers.getContent().get(0).getBusinessName(),
                equalTo("Verified Seller"));
    }

    /**
     * T105d - Test finding active sellers by rating
     */
    @Test
    void testFindActiveSellersByRating() {
        // Create sellers with different ratings
        for (int i = 0; i < 5; i++) {
            Seller seller = new Seller();
            seller.setBusinessName("Seller " + i);
            seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
            seller.setRating(new BigDecimal(4.0 + (i * 0.2)));
            sellerRepository.save(seller);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seller> sellers = sellerRepository.findActiveSellersByRating(
                new BigDecimal("4.2"),
                pageable);

        assertThat(sellers.getContent(), hasSize(greaterThan(0)));
        assertThat(sellers.getContent(), everyItem(
                hasProperty("rating", greaterThanOrEqualTo(new BigDecimal("4.2")))));
    }

    /**
     * T105e - Test search by business name
     */
    @Test
    void testSearchByBusinessName() {
        Seller seller1 = new Seller();
        seller1.setBusinessName("Happy Paws Pet Shop");
        seller1.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
        sellerRepository.save(seller1);

        Seller seller2 = new Seller();
        seller2.setBusinessName("Furry Friends Store");
        seller2.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
        sellerRepository.save(seller2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seller> results = sellerRepository.searchByBusinessName("Happy", pageable);

        assertThat(results.getContent(), hasSize(1));
        assertThat(results.getContent().get(0).getBusinessName(),
                containsString("Happy"));
    }

    /**
     * T105f - Test count by verification status
     */
    @Test
    void testCountByVerificationStatus() {
        // Create 3 verified sellers
        for (int i = 0; i < 3; i++) {
            Seller seller = new Seller();
            seller.setBusinessName("Verified " + i);
            seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
            sellerRepository.save(seller);
        }

        // Create 2 pending sellers
        for (int i = 0; i < 2; i++) {
            Seller seller = new Seller();
            seller.setBusinessName("Pending " + i);
            seller.setVerificationStatus(Seller.VerificationStatus.PENDING);
            sellerRepository.save(seller);
        }

        Long verifiedCount = sellerRepository.countByVerificationStatus(
                Seller.VerificationStatus.VERIFIED);
        Long pendingCount = sellerRepository.countByVerificationStatus(
                Seller.VerificationStatus.PENDING);

        assertThat(verifiedCount, equalTo(3L));
        assertThat(pendingCount, equalTo(2L));
    }

    /**
     * T105g - Test email verification check
     */
    @Test
    void testIsEmailVerified() {
        Seller seller = new Seller();
        seller.setBusinessName("Test Seller");
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);
        seller.setEmailVerifiedAt(java.time.LocalDateTime.now());

        Seller saved = sellerRepository.save(seller);

        Boolean isVerified = sellerRepository.isEmailVerified(saved.getId());
        assertThat(isVerified, is(true));
    }

    /**
     * T105h - Test seller profile relationship
     */
    @Test
    void testSellerProfileRelationship() {
        Seller seller = new Seller();
        seller.setBusinessName("Profile Test Seller");
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);

        SellerProfile profile = new SellerProfile();
        profile.setSeller(seller);
        profile.setBio("Professional pet breeder");
        profile.setPayoutFrequency(SellerProfile.PayoutFrequency.MONTHLY);
        seller.setProfile(profile);

        Seller saved = sellerRepository.save(seller);

        assertThat(saved.getProfile(), notNullValue());
        assertThat(saved.getProfile().getBio(), equalTo("Professional pet breeder"));

        SellerProfile foundProfile = sellerProfileRepository.findBySellerId(saved.getId()).orElse(null);
        assertThat(foundProfile, notNullValue());
        assertThat(foundProfile.getBio(), equalTo("Professional pet breeder"));
    }

    /**
     * T105i - Test bank details check
     */
    @Test
    void testHasBankDetails() {
        Seller seller = new Seller();
        seller.setBusinessName("Bank Test Seller");
        seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);

        SellerProfile profile = new SellerProfile();
        profile.setSeller(seller);
        profile.setBankAccountName("John Smith");
        profile.setBankRoutingNumber("123456789");
        profile.setBankAccountNumber("9876543210");
        seller.setProfile(profile);

        Seller saved = sellerRepository.save(seller);

        Boolean hasBankDetails = sellerProfileRepository.hasBankDetails(saved.getId());
        assertThat(hasBankDetails, is(true));
    }

    /**
     * T105j - Test verified sellers with profiles pagination
     */
    @Test
    void testFindVerifiedSellersWithProfiles() {
        for (int i = 0; i < 3; i++) {
            Seller seller = new Seller();
            seller.setBusinessName("Seller " + i);
            seller.setVerificationStatus(Seller.VerificationStatus.VERIFIED);

            SellerProfile profile = new SellerProfile();
            profile.setSeller(seller);
            profile.setBio("Bio for seller " + i);
            seller.setProfile(profile);

            sellerRepository.save(seller);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seller> sellers = sellerRepository.findVerifiedSellersWithProfiles(pageable);

        assertThat(sellers.getContent(), hasSize(3));
        assertThat(sellers.getContent(), everyItem(
                hasProperty("profile", notNullValue())));
    }
}
