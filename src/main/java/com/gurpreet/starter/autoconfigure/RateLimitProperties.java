package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * gurpreet:
 *   starter:
 *     rate-limit:
 *       enabled: true
 *       capacity: 60                    # default bucket size (requests)
 *       refill-tokens: 60
 *       refill-duration-seconds: 60
 *       exclude-paths: [/actuator/health, /swagger-ui, /v3/api-docs]
 *       strict-paths: [/api/auth/login, /api/auth/register]
 *       strict-capacity: 5               # tighter limit applied to strict-paths
 *       strict-refill-tokens: 5
 *       strict-refill-duration-seconds: 60
 *
 * NOTE: earlier versions of this class used a Map<String, PathLimit> for
 * per-path overrides, keyed by the path itself (e.g. "/api/auth/login").
 * Spring Boot's YAML binding has a known issue with Map<String,*> keys
 * that contain slashes (see spring-boot#13404) - such maps can silently
 * fail to bind, with no error, falling back to defaults. Replaced with
 * a plain List<String> (strictPaths) instead, since list VALUES don't
 * have this problem - only map KEYS do.
 *
 * In-memory bucket per client IP - fine for a single instance. If you
 * later run multiple instances behind a load balancer, each instance
 * tracks its own buckets independently. Move to a Redis-backed Bucket4j
 * proxy manager at that point if strict global limits matter.
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;

    private long capacity = 60;
    private long refillTokens = 60;
    private long refillDurationSeconds = 60;

    private List<String> excludePaths = List.of("/actuator/health", "/swagger-ui", "/v3/api-docs");

    /** Any path starting with one of these gets the stricter limit below - e.g. login/register. */
    private List<String> strictPaths = List.of("/api/auth/login", "/api/auth/register");

    private long strictCapacity = 5;
    private long strictRefillTokens = 5;
    private long strictRefillDurationSeconds = 60;
}
