package com.petstore.dto;

import java.math.BigDecimal;

/**
 * EarningsStatsDTO
 * 
 * Earnings statistics for a seller dashboard.
 */
public class EarningsStatsDTO {
    private BigDecimal totalEarnings;
    private BigDecimal monthlyEarnings;
    private BigDecimal minimumPayoutAmount;
    private String payoutFrequency;
    private BigDecimal availableForPayout;

    public EarningsStatsDTO() {
    }

    public EarningsStatsDTO(BigDecimal totalEarnings, BigDecimal monthlyEarnings,
            BigDecimal minimumPayoutAmount, String payoutFrequency,
            BigDecimal availableForPayout) {
        this.totalEarnings = totalEarnings;
        this.monthlyEarnings = monthlyEarnings;
        this.minimumPayoutAmount = minimumPayoutAmount;
        this.payoutFrequency = payoutFrequency;
        this.availableForPayout = availableForPayout;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public BigDecimal getMonthlyEarnings() {
        return monthlyEarnings;
    }

    public void setMonthlyEarnings(BigDecimal monthlyEarnings) {
        this.monthlyEarnings = monthlyEarnings;
    }

    public BigDecimal getMinimumPayoutAmount() {
        return minimumPayoutAmount;
    }

    public void setMinimumPayoutAmount(BigDecimal minimumPayoutAmount) {
        this.minimumPayoutAmount = minimumPayoutAmount;
    }

    public String getPayoutFrequency() {
        return payoutFrequency;
    }

    public void setPayoutFrequency(String payoutFrequency) {
        this.payoutFrequency = payoutFrequency;
    }

    public BigDecimal getAvailableForPayout() {
        return availableForPayout;
    }

    public void setAvailableForPayout(BigDecimal availableForPayout) {
        this.availableForPayout = availableForPayout;
    }
}
