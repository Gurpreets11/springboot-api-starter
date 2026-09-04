package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * gurpreet:
 *   starter:
 *     logging:
 *       enabled: true
 *       log-request-body: false
 *       exclude-paths: [/actuator/health, /swagger-ui, /v3/api-docs]
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.logging")
public class LoggingProperties {

    private boolean enabled = true;

    /** Careful with this in prod - request bodies may contain passwords/PII. Off by default. */
    private boolean logRequestBody = false;

    private List<String> excludePaths = List.of("/actuator/health", "/swagger-ui", "/v3/api-docs");
}
