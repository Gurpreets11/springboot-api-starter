package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bind from application.yml:
 *
 * gurpreet:
 *   starter:
 *     jwt:
 *       secret: "change-this-to-a-long-random-secret-min-32-chars"
 *       expiration-ms: 3600000        # 1 hour
 *       refresh-expiration-ms: 604800000  # 7 days
 *       header: Authorization
 *       prefix: "Bearer "
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.jwt")
public class JwtProperties {

    /** Must be at least 32 characters for HS256. Override per-environment, never commit a real secret. */
    private String secret = "REPLACE_ME_REPLACE_ME_REPLACE_ME_32CHARS_MIN";

    private long expirationMs = 3_600_000L;         // 1 hour
    private long refreshExpirationMs = 604_800_000L; // 7 days

    private String header = "Authorization";
    private String prefix = "Bearer ";
}
