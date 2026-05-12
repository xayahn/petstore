package com.petstore.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for unit tests of service classes.
 * 
 * Provides common setup with Mockito for testing business logic.
 * Extends this class for all service unit tests.
 * 
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public abstract class ServiceTestBase {

    /**
     * Common setup method for service tests.
     * Override in subclasses if additional setup is needed.
     */
    protected void setup() {
        // Override in subclasses
    }

    /**
     * Common teardown method for service tests.
     * Override in subclasses if cleanup is needed.
     */
    protected void teardown() {
        // Override in subclasses
    }
}
