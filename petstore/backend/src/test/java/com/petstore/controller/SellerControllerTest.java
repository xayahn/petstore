package com.petstore.controller;

import com.petstore.dto.SellerDTO;
import com.petstore.dto.SellerProfileDTO;
import com.petstore.service.SellerService;
import layug.exception.NotFoundException;
import layug.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SellerControllerTest
 * 
 * Controller tests for SellerController REST endpoints.
 */
@WebMvcTest(SellerController.class)
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    private SellerDTO testSeller;
    private SellerProfileDTO testProfile;

    @BeforeEach
    void setUp() {
        testSeller = new SellerDTO();
        testSeller.setId("seller-123");
        testSeller.setUserId("user-123");
        testSeller.setBusinessName("Test Pet Shop");
        testSeller.setVerificationStatus("PENDING");
        testSeller.setRating(new BigDecimal("4.50"));
        testSeller.setTotalSales(50L);

        testProfile = new SellerProfileDTO();
        testProfile.setId("profile-123");
        testProfile.setSellerId("seller-123");
        testProfile.setBio("Professional breeder");
    }

    /**
     * T110a - Test successful seller registration
     */
    @Test
    void testRegisterSeller() throws Exception {
        when(sellerService.registerSeller("user-123", "Test Pet Shop"))
                .thenReturn(testSeller);

        mockMvc.perform(post("/api/sellers/register")
                .param("userId", "user-123")
                .param("businessName", "Test Pet Shop"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo("seller-123")))
                .andExpect(jsonPath("$.business_name", equalTo("Test Pet Shop")))
                .andExpect(jsonPath("$.verification_status", equalTo("PENDING")));
    }

    /**
     * T110b - Test seller registration with empty business name
     */
    @Test
    void testRegisterSellerValidationError() throws Exception {
        when(sellerService.registerSeller("user-123", ""))
                .thenThrow(new ValidationException("Business name is required"));

        mockMvc.perform(post("/api/sellers/register")
                .param("userId", "user-123")
                .param("businessName", ""))
                .andExpect(status().isBadRequest());
    }

    /**
     * T110c - Test email verification success
     */
    @Test
    void testVerifyEmail() throws Exception {
        SellerDTO verifiedSeller = new SellerDTO();
        verifiedSeller.setId("seller-123");
        verifiedSeller.setVerificationStatus("VERIFIED");

        when(sellerService.verifySeller("token-123"))
                .thenReturn(verifiedSeller);

        mockMvc.perform(post("/api/sellers/verify-email")
                .param("token", "token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verification_status", equalTo("VERIFIED")));
    }

    /**
     * T110d - Test email verification with invalid token
     */
    @Test
    void testVerifyEmailInvalidToken() throws Exception {
        when(sellerService.verifySeller("invalid-token"))
                .thenThrow(new ValidationException("Invalid or expired token"));

        mockMvc.perform(post("/api/sellers/verify-email")
                .param("token", "invalid-token"))
                .andExpect(status().isBadRequest());
    }

    /**
     * T110e - Test get seller profile
     */
    @Test
    void testGetSeller() throws Exception {
        when(sellerService.getSellerProfile("seller-123"))
                .thenReturn(testSeller);

        mockMvc.perform(get("/api/sellers/seller-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo("seller-123")))
                .andExpect(jsonPath("$.business_name", equalTo("Test Pet Shop")))
                .andExpect(jsonPath("$.rating", equalTo(4.5)));
    }

    /**
     * T110f - Test get seller not found
     */
    @Test
    void testGetSellerNotFound() throws Exception {
        when(sellerService.getSellerProfile("nonexistent"))
                .thenThrow(new NotFoundException("Seller not found"));

        mockMvc.perform(get("/api/sellers/nonexistent"))
                .andExpect(status().isNotFound());
    }

    /**
     * T110g - Test update seller profile
     */
    @Test
    void testUpdateProfile() throws Exception {
        SellerDTO updated = new SellerDTO();
        updated.setId("seller-123");
        updated.setProfile(testProfile);

        when(sellerService.updateSellerProfile(eq("seller-123"), any(SellerProfileDTO.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/sellers/me/profile")
                .param("sellerId", "seller-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bio\": \"Professional breeder\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo("seller-123")));
    }

    /**
     * T110h - Test health check endpoint
     */
    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/sellers/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("running")));
    }
}
