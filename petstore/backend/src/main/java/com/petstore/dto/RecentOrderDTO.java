package com.petstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RecentOrderDTO
 * 
 * Recent order summary for seller dashboard.
 */
public class RecentOrderDTO {
    private String orderId;
    private String buyerName;
    private String petName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public RecentOrderDTO() {
    }

    public RecentOrderDTO(String orderId, String buyerName, String petName, BigDecimal amount, String status,
            LocalDateTime createdAt) {
        this.orderId = orderId;
        this.buyerName = buyerName;
        this.petName = petName;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
