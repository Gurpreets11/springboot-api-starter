package com.gurpreet.starter.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Small helper to grab the current logged-in user's identity anywhere
 * in the app (service layer, audit fields, etc.) without wiring
 * HttpServletRequest around.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName());
    }

    public static boolean isAuthenticated() {
        return getCurrentUsername().isPresent();
    }
}
