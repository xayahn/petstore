package com.petstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS configuration for API endpoints.
 * 
 * Allows cross-origin requests from approved domains for:
 * - Development environments (localhost)
 * - Docker networking
 * 
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * Configure CORS for the entire application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from development and Docker environments
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173", // Vite dev server (default)
                "http://localhost:3000", // Alternative dev server
                "http://localhost:8080", // Docker network
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8080"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials in CORS requests
        configuration.setAllowCredentials(true);

        // Set max age for preflight requests (1 hour)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
