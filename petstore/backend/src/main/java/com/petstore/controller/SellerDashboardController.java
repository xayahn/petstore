package com.petstore.controller;

import com.petstore.dto.SellerDashboardDTO;
import com.petstore.dto.SellerDTO;
import com.petstore.service.SellerDashboardService;
import com.petstore.service.SellerService;
import com.petstore.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import layug.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerDashboardController
 * 
 * REST endpoints for seller dashboard and analytics.
 * Provides sales stats, earnings data, and recent order information.
 */
@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Seller Dashboard", description = "Seller dashboard and analytics endpoints")
public class SellerDashboardController {

    private final SellerDashboardService dashboardService;
    private final SellerService sellerService;

    public SellerDashboardController(SellerDashboardService dashboardService, SellerService sellerService) {
        this.dashboardService = dashboardService;
        this.sellerService = sellerService;
    }

    /**
     * Get seller dashboard with sales stats, earnings, and recent orders.
     * 
     * GET /api/sellers/me/dashboard
     * 
     * Requires authentication and SELLER role.
     * Returns dashboard data aggregating:
     * - Sales statistics (total sales, active listings, revenue)
     * - Earnings statistics (total, monthly, available payout)
     * - Recent orders (last 10 orders)
     * 
     * @return Dashboard data with all stats
     */
    @GetMapping("/me/dashboard")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Get seller dashboard", description = "Get complete dashboard with sales, earnings, and order stats", responses = {
            @ApiResponse(responseCode = "200", description = "Dashboard data", content = @Content(schema = @Schema(implementation = SellerDashboardDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not a seller"),
            @ApiResponse(responseCode = "404", description = "Seller not found")
    })
    public ResponseEntity<SellerDashboardDTO> getDashboard() {
        try {
            String currentUserId = SecurityUtils.getCurrentUserId();
            com.petstore.entity.Seller seller = sellerService.getSellerByUserId(currentUserId);

            SellerDashboardDTO dashboard = dashboardService.getDashboard(seller.getId());
            return ResponseEntity.ok(dashboard);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
