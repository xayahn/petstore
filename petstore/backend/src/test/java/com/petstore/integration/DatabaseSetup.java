package com.petstore.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for database setup and cleanup in tests.
 * 
 * Provides methods for:
 * - Truncating tables before/after tests
 * - Resetting sequences
 * - Clearing test data
 * 
 * @since 1.0.0
 */
@Component
public class DatabaseSetup {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Truncate all tables in the database.
     * 
     * Useful for resetting database state between tests.
     */
    public void truncateAllTables() {
        // List of all tables in order (respecting foreign key constraints)
        String[] tables = {
                "audit_logs",
                "status_transitions",
                "seller_earnings",
                "payouts",
                "reviews",
                "seller_reviews",
                "payments",
                "order_items",
                "orders",
                "wishlist_items",
                "wishlists",
                "cart_items",
                "carts",
                "pet_images",
                "pets",
                "seller_profiles",
                "sellers",
                "shipping_addresses",
                "users"
        };

        // Disable foreign key checks
        jdbcTemplate.execute("SET session_replication_role = REPLICA");

        try {
            for (String table : tables) {
                truncateTable(table);
            }
        } finally {
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = DEFAULT");
        }
    }

    /**
     * Truncate a single table and reset its sequences.
     * 
     * @param tableName Name of the table to truncate
     */
    public void truncateTable(String tableName) {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
        } catch (Exception e) {
            // Table might not exist or be already empty - ignore
        }
    }

    /**
     * Reset all sequences to 1.
     */
    public void resetAllSequences() {
        String[] sequences = {
                "order_number_seq",
                "invoice_number_seq"
        };

        for (String sequence : sequences) {
            resetSequence(sequence);
        }
    }

    /**
     * Reset a single sequence to start value.
     * 
     * @param sequenceName Name of the sequence
     */
    public void resetSequence(String sequenceName) {
        try {
            jdbcTemplate.execute("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1");
        } catch (Exception e) {
            // Sequence might not exist - ignore
        }
    }

    /**
     * Clear all data and reset sequences.
     * 
     * This is useful for complete database reset between test classes.
     */
    public void resetDatabase() {
        truncateAllTables();
        resetAllSequences();
    }
}
