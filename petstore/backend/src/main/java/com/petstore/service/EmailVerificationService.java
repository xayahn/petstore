package com.petstore.service;

import com.petstore.entity.Seller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EmailVerificationService
 * 
 * Handles email verification token generation, validation, and sending.
 * 
 * Note: This is a placeholder implementation.
 * In production, integrate with an actual email service (e.g., SendGrid, AWS
 * SES)
 * and a persistent token store (database table).
 */
@Slf4j
@Service
public class EmailVerificationService {

    // Temporary in-memory token store (should be database in production)
    private final Map<String, EmailVerificationToken> tokenStore = new HashMap<>();

    private static final long TOKEN_EXPIRY_MINUTES = 24 * 60; // 24 hours

    /**
     * Email verification token class
     */
    private static class EmailVerificationToken {
        String token;
        String sellerId;
        Instant createdAt;

        EmailVerificationToken(String token, String sellerId) {
            this.token = token;
            this.sellerId = sellerId;
            this.createdAt = Instant.now();
        }

        boolean isExpired() {
            long ageMinutes = java.time.Duration.between(createdAt, Instant.now()).toMinutes();
            return ageMinutes > TOKEN_EXPIRY_MINUTES;
        }
    }

    /**
     * Generate verification token for seller
     */
    public String generateVerificationToken(Seller seller) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, new EmailVerificationToken(token, seller.getId()));

        log.info("Generated verification token for seller: sellerId={}", seller.getId());
        return token;
    }

    /**
     * Send verification email to seller
     * 
     * Note: This is a placeholder. In production, send actual email via
     * SendGrid/AWS SES.
     */
    public void sendVerificationEmail(Seller seller) {
        String token = generateVerificationToken(seller);

        log.info("Sending verification email to seller: sellerId={}, email={}, token={}",
                seller.getId(), seller.getBusinessName(), token.substring(0, 8) + "...");

        // TODO: Send actual email
        // String verificationUrl = String.format("%s/verify-email?token=%s", baseUrl,
        // token);
        // emailClient.send(seller.getEmail(), "Verify Your Seller Account",
        // String.format("Click here to verify: %s", verificationUrl));
    }

    /**
     * Verify token and return seller ID if valid
     */
    public String verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        EmailVerificationToken verificationToken = tokenStore.get(token);
        if (verificationToken == null) {
            log.warn("Verification token not found: token={}", token);
            return null;
        }

        if (verificationToken.isExpired()) {
            tokenStore.remove(token);
            log.warn("Verification token expired: token={}", token);
            return null;
        }

        tokenStore.remove(token); // One-time use
        log.info("Token verified successfully: sellerId={}", verificationToken.sellerId);
        return verificationToken.sellerId;
    }

    /**
     * Check if seller has verified email
     */
    public boolean isEmailVerified(Seller seller) {
        return seller.getEmailVerifiedAt() != null;
    }
}
