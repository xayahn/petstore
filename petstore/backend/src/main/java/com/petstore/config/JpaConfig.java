package com.petstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.petstore.security.SecurityUtils;
import java.util.Optional;

/**
 * JPA configuration for entity auditing.
 * 
 * Enables automatic tracking of:
 * - createdAt: Timestamp when entity was created
 * - updatedAt: Timestamp when entity was last modified
 * - createdBy: User ID who created the entity
 * - updatedBy: User ID who last modified the entity
 * 
 * @since 1.0.0
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    /**
     * Configure auditor aware for capturing current user in audit fields.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityUtils.getCurrentUserId())
                .or(() -> Optional.of("system"));
    }
}
