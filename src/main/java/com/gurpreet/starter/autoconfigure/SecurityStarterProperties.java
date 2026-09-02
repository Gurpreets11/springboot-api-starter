package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * gurpreet:
 *   starter:
 *     security:
 *       public-endpoints:
 *         - /api/auth/**
 *         - /swagger-ui/**
 *         - /v3/api-docs/**
 *       stateless: true
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.security")
public class SecurityStarterProperties {

    /** Endpoints that should NOT require a valid JWT. */
    private List<String> publicEndpoints = List.of(
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health"
    );

    /** Session policy — almost always true for a JWT/REST API. */
    private boolean stateless = true;

    /** Whether CSRF should be disabled (standard for stateless JWT APIs). */
    private boolean csrfDisabled = true;
}
