package com.petstore.integration;

import java.lang.annotation.*;

/**
 * Marker annotation for integration tests.
 * 
 * Use this annotation on integration test classes to distinguish them
 * from unit tests. Allows for filtering tests during build:
 * 
 * mvn test -Dgroups="integration"
 * mvn test -DexcludedGroups="integration"
 * 
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntegrationTest {
    /**
     * Optional description of the test.
     */
    String value() default "";
}
