package com.petstore.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security utility methods for extracting user information from Spring Security
 * context.
 * 
 * Provides helper methods to:
 * - Extract current authenticated user ID
 * - Get current authentication
 * - Check if user is authenticated
 * 
 * @since 1.0.0
 */
@Component
public class SecurityUtils {

    /**
     * Get the ID of the currently authenticated user.
     * 
     * @return User ID from principal, or null if not authenticated
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
        }

        return null;
    }

    /**
     * Check if a user is currently authenticated.
     * 
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Get the current authentication object.
     * 
     * @return Current authentication, or null if not authenticated
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
