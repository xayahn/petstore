package com.petstore.dto;

import java.math.BigDecimal;

/**
 * SalesStatsDTO
 * 
 * Aggregated sales statistics for a seller dashboard.
 */
public class SalesStatsDTO {
    private Long totalSales;
    private Long activeListings;
    private BigDecimal totalRevenue;
    private Double averageRating;

    public SalesStatsDTO() {
    }

    public SalesStatsDTO(Long totalSales, Long activeListings, BigDecimal totalRevenue, Double averageRating) {
        this.totalSales = totalSales;
        this.activeListings = activeListings;
        this.totalRevenue = totalRevenue;
        this.averageRating = averageRating;
    }

    public Long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Long totalSales) {
        this.totalSales = totalSales;
    }

    public Long getActiveListings() {
        return activeListings;
    }

    public void setActiveListings(Long activeListings) {
        this.activeListings = activeListings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
