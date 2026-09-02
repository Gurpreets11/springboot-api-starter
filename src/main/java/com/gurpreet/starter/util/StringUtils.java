package com.gurpreet.starter.util;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.regex.Pattern;

public final class StringUtils {

    private StringUtils() {
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern INDIA_MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidIndianMobile(String mobile) {
        return mobile != null && INDIA_MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    public static String mask(String value, int visibleChars) {
        if (isBlank(value) || value.length() <= visibleChars) return value;
        return "*".repeat(value.length() - visibleChars) + value.substring(value.length() - visibleChars);
    }
}
