package com.example.ecommerceapigateway.security;

import org.springframework.util.AntPathMatcher;
import java.util.List;

/**
 * Central place to declare what is public (no JWT required).
 * Kept in code so it's easy to see at a glance during review.
 */
public final class PublicEndpoints {

    private PublicEndpoints() {}

    /**
     * All requests matching these patterns bypass JWT validation.
     * Must stay in sync with user-service's security config.
     */
    public static final List<String> PATHS = List.of(
            // ---- Auth ----
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/verify-email",
            "/api/auth/oauth2/**",

            // ---- Public catalogue browsing (guests) ----
            "/api/products",
            "/api/products/**",
            "/api/categories",
            "/api/categories/**",
            "/api/reviews/product/**",

            // ---- Vendor registration ----
            "/api/vendors/register",

            // ---- Payment webhooks (Razorpay) ----
            "/api/payments/webhook/**",

            // ---- Health / docs ----
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public static boolean isPublic(String path) {
        return PATHS.stream().anyMatch(p -> MATCHER.match(p, path));
    }
}