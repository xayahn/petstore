package com.petstore.service;

import com.petstore.dto.EarningsStatsDTO;
import com.petstore.dto.RecentOrderDTO;
import com.petstore.dto.SalesStatsDTO;
import com.petstore.dto.SellerDashboardDTO;
import com.petstore.entity.Seller;
import com.petstore.entity.SellerProfile;
import com.petstore.repository.PetRepository;
import com.petstore.repository.SellerRepository;
import layug.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SellerDashboardService
 * 
 * Aggregates dashboard metrics and statistics for sellers.
 * Provides sales stats, earnings data, and recent order information.
 */
@Slf4j
@Service
public class SellerDashboardService {

    private final SellerRepository sellerRepository;
    private final PetRepository petRepository;

    public SellerDashboardService(SellerRepository sellerRepository, PetRepository petRepository) {
        this.sellerRepository = sellerRepository;
        this.petRepository = petRepository;
    }

    /**
     * Get complete dashboard data for a seller
     * 
     * @param sellerId Seller ID
     * @return Complete dashboard with stats, earnings, and recent orders
     */
    @Transactional(readOnly = true)
    public SellerDashboardDTO getDashboard(String sellerId) {
        log.info("Fetching dashboard for seller: {}", sellerId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found: " + sellerId));

        SalesStatsDTO salesStats = getSalesStats(sellerId);
        EarningsStatsDTO earningsStats = getEarningsStats(sellerId, seller);
        List<RecentOrderDTO> recentOrders = getRecentOrders(sellerId);

        SellerDashboardDTO dashboard = new SellerDashboardDTO();
        dashboard.setSalesStats(salesStats);
        dashboard.setEarningsStats(earningsStats);
        dashboard.setRecentOrders(recentOrders);

        log.info("Dashboard fetched successfully for seller: {}", sellerId);
        return dashboard;
    }

    /**
     * Get sales statistics for a seller
     * 
     * @param sellerId Seller ID
     * @return Sales stats including total sales, active listings, revenue
     */
    @Transactional(readOnly = true)
    public SalesStatsDTO getSalesStats(String sellerId) {
        log.debug("Calculating sales stats for seller: {}", sellerId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found: " + sellerId));

        // Get count of active (available) pets for this seller
        Long activeListings = petRepository.countBySellerIdAndAvailabilityStatus(sellerId, "available");
        if (activeListings == null) {
            activeListings = 0L;
        }

        // Get seller's total sales from entity
        Long totalSales = seller.getTotalSales() != null ? seller.getTotalSales() : 0L;

        // Calculate total revenue: (total sales count * average price of available
        // pets)
        // or use totalEarnings from seller
        BigDecimal totalRevenue = seller.getTotalEarnings() != null ? seller.getTotalEarnings() : BigDecimal.ZERO;

        // Get seller's rating
        Double averageRating = seller.getRating() != null ? seller.getRating().doubleValue() : 0.0;

        SalesStatsDTO stats = new SalesStatsDTO();
        stats.setTotalSales(totalSales);
        stats.setActiveListings(activeListings);
        stats.setTotalRevenue(totalRevenue);
        stats.setAverageRating(averageRating);

        return stats;
    }

    /**
     * Get earnings statistics for a seller
     * 
     * @param sellerId Seller ID
     * @param seller   Seller entity
     * @return Earnings stats including total, monthly, and available payout
     */
    @Transactional(readOnly = true)
    public EarningsStatsDTO getEarningsStats(String sellerId, Seller seller) {
        log.debug("Calculating earnings stats for seller: {}", sellerId);

        SellerProfile profile = seller.getProfile();

        BigDecimal totalEarnings = seller.getTotalEarnings() != null ? seller.getTotalEarnings() : BigDecimal.ZERO;

        // Monthly earnings: for now, estimate as 1/12 of total (placeholder)
        BigDecimal monthlyEarnings = totalEarnings.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);

        BigDecimal minimumPayoutAmount = BigDecimal.ZERO;
        String payoutFrequency = "MONTHLY";

        if (profile != null) {
            minimumPayoutAmount = profile.getMinimumPayoutAmount() != null
                    ? profile.getMinimumPayoutAmount()
                    : new BigDecimal("10.00");
            payoutFrequency = profile.getPayoutFrequency() != null
                    ? profile.getPayoutFrequency().toString()
                    : "MONTHLY";
        }

        // Available for payout: total earnings if above minimum
        BigDecimal availableForPayout = totalEarnings.compareTo(minimumPayoutAmount) >= 0
                ? totalEarnings
                : BigDecimal.ZERO;

        EarningsStatsDTO stats = new EarningsStatsDTO();
        stats.setTotalEarnings(totalEarnings);
        stats.setMonthlyEarnings(monthlyEarnings);
        stats.setMinimumPayoutAmount(minimumPayoutAmount);
        stats.setPayoutFrequency(payoutFrequency);
        stats.setAvailableForPayout(availableForPayout);

        return stats;
    }

    /**
     * Get recent orders for a seller
     * 
     * NOTE: Placeholder implementation. To be replaced when Order entity is
     * created.
     * Currently returns empty list as Orders haven't been implemented yet.
     * 
     * @param sellerId Seller ID
     * @return List of recent orders (max 10)
     */
    @Transactional(readOnly = true)
    public List<RecentOrderDTO> getRecentOrders(String sellerId) {
        log.debug("Fetching recent orders for seller: {}", sellerId);

        // TODO: Implement when Order entity is created
        // Example query would be:
        // List<Order> orders = orderRepository.findBySellerIdOrderByCreatedAtDesc(
        // sellerId, PageRequest.of(0, 10));

        // Placeholder: return empty list for now
        List<RecentOrderDTO> recentOrders = new ArrayList<>();

        log.debug("Fetched {} recent orders for seller: {}", recentOrders.size(), sellerId);
        return recentOrders;
    }
}
