package com.gurpreet.starter.logging;

import com.gurpreet.starter.autoconfigure.LoggingProperties;
import com.gurpreet.starter.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Logs one line per request: method, URI, status, duration, current user.
 * Also stamps every log line during the request with a correlation/request
 * ID via MDC - either the incoming X-Request-Id header, or a freshly
 * generated one - so you can grep one request's full log trail even under
 * concurrent traffic.
 *
 * Toggle off entirely, or exclude specific paths, via
 * gurpreet.starter.logging.* properties.
 */
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_REQUEST_ID_KEY = "requestId";
    private static final String MDC_USER_KEY = "user";

    private final LoggingProperties loggingProperties;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!loggingProperties.isEnabled()) {
            return true;
        }
        String path = request.getRequestURI();
        return loggingProperties.getExcludePaths().stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader(REQUEST_ID_HEADER, requestId);

        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            // set AFTER the chain runs - by now Spring Security has populated the auth context
            SecurityUtils.getCurrentUsername().ifPresent(u -> MDC.put(MDC_USER_KEY, u));

            long durationMs = System.currentTimeMillis() - start;
            log.info("{} {} -> {} ({} ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs);

            MDC.remove(MDC_REQUEST_ID_KEY);
            MDC.remove(MDC_USER_KEY);
        }
    }
}
