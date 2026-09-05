package com.gurpreet.starter.ratelimit;

import com.gurpreet.starter.autoconfigure.RateLimitProperties;
import com.gurpreet.starter.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory, per-IP token bucket rate limiter. Applies a default
 * limit to every request, with a stricter shared limit applied to any
 * path listed in strict-paths (e.g. /api/auth/login) to slow down
 * brute-force attempts.
 *
 * Ordered to run BEFORE Spring Security's filter chain, so abusive
 * requests are rejected before any authentication work happens.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!rateLimitProperties.isEnabled()) {
            return true;
        }
        String path = request.getRequestURI();
        return rateLimitProperties.getExcludePaths().stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean strict = isStrictPath(path);
        String clientKey = resolveClientKey(request) + "|" + (strict ? "strict" : "default");

        Bucket bucket = buckets.computeIfAbsent(clientKey, k -> newBucket(strict));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for client [{}] on path [{}] (strict={})",
                    resolveClientKey(request), path, strict);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            BaseResponse<Object> body = BaseResponse.error(
                    "Too many requests - please slow down and try again shortly",
                    HttpStatus.TOO_MANY_REQUESTS.value());
            objectMapper.writeValue(response.getWriter(), body);
        }
    }

    private boolean isStrictPath(String path) {
        return rateLimitProperties.getStrictPaths().stream().anyMatch(path::startsWith);
    }

    private String resolveClientKey(HttpServletRequest request) {
        // If behind a proxy/load balancer, prefer X-Forwarded-For (first hop) over remoteAddr.
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Bucket newBucket(boolean strict) {
        long capacity = strict ? rateLimitProperties.getStrictCapacity() : rateLimitProperties.getCapacity();
        long refillTokens = strict ? rateLimitProperties.getStrictRefillTokens() : rateLimitProperties.getRefillTokens();
        long refillSeconds = strict ? rateLimitProperties.getStrictRefillDurationSeconds() : rateLimitProperties.getRefillDurationSeconds();

        Bandwidth limit = Bandwidth.classic(capacity,
                Refill.greedy(refillTokens, Duration.ofSeconds(refillSeconds)));
        return Bucket.builder().addLimit(limit).build();
    }
}
