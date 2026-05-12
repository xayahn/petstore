package com.petstore.controller;

import com.petstore.dto.EarningsStatsDTO;
import com.petstore.dto.RecentOrderDTO;
import com.petstore.dto.SalesStatsDTO;
import com.petstore.dto.SellerDashboardDTO;
import com.petstore.entity.Seller;
import com.petstore.service.SellerDashboardService;
import com.petstore.service.SellerService;
import layug.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SellerDashboardController.
 * 
 * Tests dashboard endpoint with mocked service layer.
 */
@WebMvcTest(SellerDashboardController.class)
@DisplayName("SellerDashboardController Unit Tests")
class SellerDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerDashboardService dashboardService;

    @MockBean
    private SellerService sellerService;

    private SellerDashboardDTO testDashboard;
    private SalesStatsDTO testSalesStats;
    private EarningsStatsDTO testEarningsStats;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId("seller-1");
        testSeller.setUserId("user-1");
        testSeller.setBusinessName("Test Pet Shop");

        testSalesStats = new SalesStatsDTO();
        testSalesStats.setTotalSales(25L);
        testSalesStats.setActiveListings(10L);
        testSalesStats.setTotalRevenue(new BigDecimal("2500.00"));
        testSalesStats.setAverageRating(4.5);

        testEarningsStats = new EarningsStatsDTO();
        testEarningsStats.setTotalEarnings(new BigDecimal("2500.00"));
        testEarningsStats.setMonthlyEarnings(new BigDecimal("208.33"));
        testEarningsStats.setMinimumPayoutAmount(new BigDecimal("10.00"));
        testEarningsStats.setPayoutFrequency("MONTHLY");
        testEarningsStats.setAvailableForPayout(new BigDecimal("2500.00"));

        List<RecentOrderDTO> recentOrders = new ArrayList<>();

        testDashboard = new SellerDashboardDTO();
        testDashboard.setSalesStats(testSalesStats);
        testDashboard.setEarningsStats(testEarningsStats);
        testDashboard.setRecentOrders(recentOrders);
    }

    @Test
    @DisplayName("Should return dashboard for authenticated seller")
    void testGetDashboard() throws Exception {
        // Arrange
        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-1");
        when(sellerService.getSellerByUserId("user-1")).thenReturn(testSeller);
        when(dashboardService.getDashboard("seller-1")).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/sellers/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesStats.totalSales", equalTo(25)))
                .andExpect(jsonPath("$.salesStats.activeListings", equalTo(10)))
                .andExpect(jsonPath("$.salesStats.totalRevenue", comparesEqualTo(2500.00)))
                .andExpect(jsonPath("$.salesStats.averageRating", equalTo(4.5)))
                .andExpect(jsonPath("$.earningsStats.totalEarnings", comparesEqualTo(2500.00)))
                .andExpect(jsonPath("$.earningsStats.payoutFrequency", equalTo("MONTHLY")))
                .andExpect(jsonPath("$.recentOrders", hasSize(0)));
    }

    @Test
    @DisplayName("Should return dashboard with recent orders")
    void testGetDashboardWithOrders() throws Exception {
        // Arrange
        List<RecentOrderDTO> orders = new ArrayList<>();
        RecentOrderDTO order1 = new RecentOrderDTO(
                "order-1", "John Buyer", "Buddy",
                new BigDecimal("1000.00"), "completed",
                java.time.LocalDateTime.now());
        orders.add(order1);

        testDashboard.setRecentOrders(orders);

        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-1");
        when(sellerService.getSellerByUserId("user-1")).thenReturn(testSeller);
        when(dashboardService.getDashboard("seller-1")).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/sellers/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recentOrders", hasSize(1)))
                .andExpect(jsonPath("$.recentOrders[0].orderId", equalTo("order-1")))
                .andExpect(jsonPath("$.recentOrders[0].buyerName", equalTo("John Buyer")))
                .andExpect(jsonPath("$.recentOrders[0].petName", equalTo("Buddy")));
    }

    @Test
    @DisplayName("Should return 404 when seller not found")
    void testGetDashboardSellerNotFound() throws Exception {
        // Arrange
        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-999");
        when(sellerService.getSellerByUserId("user-999"))
                .thenThrow(new NotFoundException("Seller not found"));

        // Act & Assert
        mockMvc.perform(get("/api/sellers/me/dashboard"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should aggregate sales statistics correctly")
    void testSalesStatsAggregation() throws Exception {
        // Arrange
        SalesStatsDTO zeroSalesStats = new SalesStatsDTO();
        zeroSalesStats.setTotalSales(0L);
        zeroSalesStats.setActiveListings(5L);
        zeroSalesStats.setTotalRevenue(BigDecimal.ZERO);
        zeroSalesStats.setAverageRating(0.0);

        testDashboard.setSalesStats(zeroSalesStats);

        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-1");
        when(sellerService.getSellerByUserId("user-1")).thenReturn(testSeller);
        when(dashboardService.getDashboard("seller-1")).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/sellers/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesStats.totalSales", equalTo(0)))
                .andExpect(jsonPath("$.salesStats.activeListings", equalTo(5)))
                .andExpect(jsonPath("$.salesStats.totalRevenue", comparesEqualTo(0.0)));
    }

    @Test
    @DisplayName("Should calculate earnings correctly with minimum payout threshold")
    void testEarningsCalculationWithMinimum() throws Exception {
        // Arrange
        EarningsStatsDTO lowEarningsStats = new EarningsStatsDTO();
        lowEarningsStats.setTotalEarnings(new BigDecimal("5.00"));
        lowEarningsStats.setMonthlyEarnings(new BigDecimal("0.42"));
        lowEarningsStats.setMinimumPayoutAmount(new BigDecimal("10.00"));
        lowEarningsStats.setPayoutFrequency("MONTHLY");
        lowEarningsStats.setAvailableForPayout(BigDecimal.ZERO); // Below minimum

        testDashboard.setEarningsStats(lowEarningsStats);

        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-1");
        when(sellerService.getSellerByUserId("user-1")).thenReturn(testSeller);
        when(dashboardService.getDashboard("seller-1")).thenReturn(testDashboard);

        // Act & Assert
        mockMvc.perform(get("/api/sellers/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.earningsStats.totalEarnings", comparesEqualTo(5.00)))
                .andExpect(jsonPath("$.earningsStats.minumumPayoutAmount", comparesEqualTo(10.00)))
                .andExpect(jsonPath("$.earningsStats.availableForPayout", comparesEqualTo(0.00)));
    }
}
