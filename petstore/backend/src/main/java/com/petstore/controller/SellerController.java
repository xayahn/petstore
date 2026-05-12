package com.petstore.controller;

import com.petstore.dto.SellerDTO;
import com.petstore.dto.SellerProfileDTO;
import com.petstore.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * SellerController
 * 
 * REST API endpoints for seller operations.
 * 
 * Endpoints:
 * - POST /api/sellers/register - Register as seller
 * - POST /api/sellers/verify-email - Verify email with token
 * - GET /api/sellers/{id} - Get seller profile (public)
 * - PUT /api/sellers/me/profile - Update seller profile (authenticated)
 */
@Slf4j
@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Sellers", description = "Seller account management and profiles")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    /**
     * Register as seller
     * 
     * POST /api/sellers/register
     * Requires authentication. Existing user becomes a seller.
     */
    @PostMapping("/register")
    @Operation(summary = "Register as seller", description = "Convert current user account to seller account")
    @ApiResponse(responseCode = "201", description = "Seller registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public ResponseEntity<SellerDTO> registerSeller(
            @RequestParam String userId,
            @RequestParam String businessName) {

        log.info("Registering seller: userId={}", userId);

        SellerDTO seller = sellerService.registerSeller(userId, businessName);
        return ResponseEntity.status(HttpStatus.CREATED).body(seller);
    }

    /**
     * Verify seller email
     * 
     * POST /api/sellers/verify-email
     * Public endpoint. Verifies seller with token from email.
     */
    @PostMapping("/verify-email")
    @Operation(summary = "Verify seller email", description = "Verify seller email address with token from verification email")
    @ApiResponse(responseCode = "200", description = "Email verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    public ResponseEntity<SellerDTO> verifyEmail(@RequestParam String token) {
        log.info("Verifying seller email with token");

        SellerDTO seller = sellerService.verifySeller(token);
        return ResponseEntity.ok(seller);
    }

    /**
     * Get seller profile by ID
     * 
     * GET /api/sellers/{id}
     * Public endpoint. Returns seller information for public profile display.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get seller profile", description = "Get public seller profile information")
    @ApiResponse(responseCode = "200", description = "Seller found")
    @ApiResponse(responseCode = "404", description = "Seller not found")
    public ResponseEntity<SellerDTO> getSeller(@PathVariable String id) {
        log.debug("Fetching seller: id={}", id);

        SellerDTO seller = sellerService.getSellerProfile(id);
        return ResponseEntity.ok(seller);
    }

    /**
     * Update seller profile
     * 
     * PUT /api/sellers/me/profile
     * Authenticated endpoint. Updates current seller's profile.
     */
    @PutMapping("/me/profile")
    @Operation(summary = "Update seller profile", description = "Update current seller's profile information")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    public ResponseEntity<SellerDTO> updateProfile(
            @RequestParam String sellerId,
            @Valid @RequestBody SellerProfileDTO profileDto) {

        log.info("Updating seller profile: sellerId={}", sellerId);

        SellerDTO seller = sellerService.updateSellerProfile(sellerId, profileDto);
        return ResponseEntity.ok(seller);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if seller service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Seller service is running");
    }
}
