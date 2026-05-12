package com.petstore;

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
 * 
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class PetstoreApplication {

    /**
     * Application main entry point.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PetstoreApplication.class, args);
    }
}
