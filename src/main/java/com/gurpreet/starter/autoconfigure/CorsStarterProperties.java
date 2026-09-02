package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * gurpreet:
 *   starter:
 *     cors:
 *       allowed-origins: ["https://app.kenstar.com", "http://localhost:4200"]
 *       allowed-methods: [GET, POST, PUT, PATCH, DELETE, OPTIONS]
 *       allowed-headers: ["*"]
 *       allow-credentials: true
 *       max-age-seconds: 3600
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.cors")
public class CorsStarterProperties {

    private List<String> allowedOrigins = List.of("http://localhost:4200", "http://localhost:3000");
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = List.of("*");
    private List<String> exposedHeaders = List.of("Authorization");
    private boolean allowCredentials = true;
    private long maxAgeSeconds = 3600L;
}
