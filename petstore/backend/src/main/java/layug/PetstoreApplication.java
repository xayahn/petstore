package layug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application entry point for Petstore backend.
 * 
 * This application provides the REST API for a peer-to-peer pet marketplace
 * with features for browsing, selling, and purchasing pets.
 * 
 * Key features:
 * - JWT-based authentication and authorization
 * - Multi-seller support with email verification
 * - Shopping cart and order management
 * - Stripe payment integration
 * - Commission-based payout system
 * - Email notifications and verification
 * 
 * Package Structure (Layered Architecture):
 * layug.rest - REST Controllers (API endpoints)
 * layug.service - Business logic layer
 * layug.repository - Data access layer (Spring Data JPA)
 * layug.entity - JPA entity models
 * layug.dto - Data Transfer Objects
 * layug.config - Spring configuration beans
 * layug.security - Security configuration and utilities
 * layug.exception - Custom exception handling
 * layug.util - Utility classes and helpers
 */
@SpringBootApplication(scanBasePackages = "layug")
@EnableAsync
public class PetstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetstoreApplication.class, args);
    }
}
