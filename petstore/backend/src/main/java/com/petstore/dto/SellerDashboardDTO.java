package com.petstore.dto;

import java.util.List;

/**
 * SellerDashboardDTO
 * 
 * Complete dashboard data for a seller including sales stats, recent orders,
 * and earnings.
 */
public class SellerDashboardDTO {
    private SalesStatsDTO salesStats;
    private EarningsStatsDTO earningsStats;
    private List<RecentOrderDTO> recentOrders;

    public SellerDashboardDTO() {
    }

    public SellerDashboardDTO(SalesStatsDTO salesStats, EarningsStatsDTO earningsStats,
            List<RecentOrderDTO> recentOrders) {
        this.salesStats = salesStats;
        this.earningsStats = earningsStats;
        this.recentOrders = recentOrders;
    }

    public SalesStatsDTO getSalesStats() {
        return salesStats;
    }

    public void setSalesStats(SalesStatsDTO salesStats) {
        this.salesStats = salesStats;
    }

    public EarningsStatsDTO getEarningsStats() {
        return earningsStats;
    }

    public void setEarningsStats(EarningsStatsDTO earningsStats) {
        this.earningsStats = earningsStats;
    }

    public List<RecentOrderDTO> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrderDTO> recentOrders) {
        this.recentOrders = recentOrders;
    }
}
