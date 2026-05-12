package com.petstore.service;

import com.petstore.dto.EarningsStatsDTO;
import com.petstore.dto.SalesStatsDTO;
import com.petstore.dto.SellerDashboardDTO;
import com.petstore.entity.Seller;
import com.petstore.entity.SellerProfile;
import com.petstore.repository.PetRepository;
import com.petstore.repository.SellerRepository;
import layug.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SellerDashboardService.
 * 
 * Tests business logic for dashboard metrics and statistics.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerDashboardService Unit Tests")
class SellerDashboardServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private PetRepository petRepository;

    private SellerDashboardService dashboardService;
    private Seller testSeller;
    private SellerProfile testProfile;

    @BeforeEach
    void setUp() {
        dashboardService = new SellerDashboardService(sellerRepository, petRepository);

        testProfile = new SellerProfile();
        testProfile.setId("profile-1");
        testProfile.setMinimumPayoutAmount(new BigDecimal("10.00"));
        testProfile.setPayoutFrequency(SellerProfile.PayoutFrequency.MONTHLY);

        testSeller = new Seller();
        testSeller.setId("seller-1");
        testSeller.setUserId("user-1");
        testSeller.setBusinessName("Test Pet Shop");
        testSeller.setTotalSales(25L);
        testSeller.setTotalEarnings(new BigDecimal("2500.00"));
        testSeller.setRating(new BigDecimal("4.5"));
        testSeller.setProfile(testProfile);
    }

    @Test
    @DisplayName("Should get complete dashboard for seller")
    void testGetDashboard() {
        // Arrange
        when(sellerRepository.findById("seller-1")).thenReturn(Optional.of(testSeller));
        when(petRepository.countBySellerIdAndAvailabilityStatus("seller-1", "available")).thenReturn(10L);

        // Act
        SellerDashboardDTO dashboard = dashboardService.getDashboard("seller-1");

        // Assert
        assertThat(dashboard, notNullValue());
        assertThat(dashboard.getSalesStats(), notNullValue());
        assertThat(dashboard.getEarningsStats(), notNullValue());
        assertThat(dashboard.getRecentOrders(), notNullValue());
    }

    @Test
    @DisplayName("Should throw NotFoundException for non-existent seller")
    void testGetDashboardNotFound() {
        // Arrange
        when(sellerRepository.findById("seller-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            dashboardService.getDashboard("seller-999");
        });
    }

    @Test
    @DisplayName("Should calculate sales stats correctly")
    void testGetSalesStats() {
        // Arrange
        when(sellerRepository.findById("seller-1")).thenReturn(Optional.of(testSeller));
        when(petRepository.countBySellerIdAndAvailabilityStatus("seller-1", "available")).thenReturn(10L);

        // Act
        SalesStatsDTO stats = dashboardService.getSalesStats("seller-1");

        // Assert
        assertThat(stats.getTotalSales(), equalTo(25L));
        assertThat(stats.getActiveListings(), equalTo(10L));
        assertThat(stats.getTotalRevenue(), comparesEqualTo(new BigDecimal("2500.00")));
        assertThat(stats.getAverageRating(), equalTo(4.5));
    }

    @Test
    @DisplayName("Should handle null earnings in sales stats")
    void testGetSalesStatsNullEarnings() {
        // Arrange
        testSeller.setTotalEarnings(null);
        testSeller.setRating(null);
        when(sellerRepository.findById("seller-1")).thenReturn(Optional.of(testSeller));
        when(petRepository.countBySellerIdAndAvailabilityStatus("seller-1", "available")).thenReturn(5L);

        // Act
        SalesStatsDTO stats = dashboardService.getSalesStats("seller-1");

        // Assert
        assertThat(stats.getTotalRevenue(), comparesEqualTo(BigDecimal.ZERO));
        assertThat(stats.getAverageRating(), equalTo(0.0));
    }

    @Test
    @DisplayName("Should calculate earnings stats correctly")
    void testGetEarningsStats() {
        // Act
        EarningsStatsDTO stats = dashboardService.getEarningsStats("seller-1", testSeller);

        // Assert
        assertThat(stats.getTotalEarnings(), comparesEqualTo(new BigDecimal("2500.00")));
        assertThat(stats.getMonthlyEarnings(), comparesEqualTo(new BigDecimal("208.33")));
        assertThat(stats.getMinimumPayoutAmount(), comparesEqualTo(new BigDecimal("10.00")));
        assertThat(stats.getPayoutFrequency(), equalTo("MONTHLY"));
        assertThat(stats.getAvailableForPayout(), comparesEqualTo(new BigDecimal("2500.00")));
    }

    @Test
    @DisplayName("Should set available payout to zero when below minimum")
    void testGetEarningsStatsBelowMinimum() {
        // Arrange
        testSeller.setTotalEarnings(new BigDecimal("5.00"));

        // Act
        EarningsStatsDTO stats = dashboardService.getEarningsStats("seller-1", testSeller);

        // Assert
        assertThat(stats.getTotalEarnings(), comparesEqualTo(new BigDecimal("5.00")));
        assertThat(stats.getAvailableForPayout(), comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle seller with no profile")
    void testGetEarningsStatsNoProfile() {
        // Arrange
        testSeller.setProfile(null);

        // Act
        EarningsStatsDTO stats = dashboardService.getEarningsStats("seller-1", testSeller);

        // Assert
        assertThat(stats.getMinimumPayoutAmount(), comparesEqualTo(new BigDecimal("10.00")));
        assertThat(stats.getPayoutFrequency(), equalTo("MONTHLY"));
    }

    @Test
    @DisplayName("Should get recent orders (placeholder implementation)")
    void testGetRecentOrders() {
        // Act
        var orders = dashboardService.getRecentOrders("seller-1");

        // Assert
        assertThat(orders, notNullValue());
        // Placeholder returns empty list
        assertThat(orders, hasSize(0));
    }
}
