package com.example.ecommerceapigateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit route definitions. Using lb:// URIs so Kubernetes discovery + LoadBalancer
 * pick a healthy pod per request.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // ---------- Auth + Users + Vendors ----------
                .route("user-service", r -> r
                        .path("/api/auth/**", "/api/users/**", "/api/vendors/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("user-serviceCB")
                                        .setFallbackUri("forward:/fallback/user-service"))
                                .retry(retry -> retry
                                        .setRetries(2)
                                        .setStatuses(org.springframework.http.HttpStatus.BAD_GATEWAY,
                                                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE)))
                        .uri("lb://user-service"))

                // ---------- Products + Categories + Reviews ----------
                .route("product-service", r -> r
                        .path("/api/products/**", "/api/categories/**", "/api/reviews/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("product-serviceCB")
                                        .setFallbackUri("forward:/fallback/product-service")))
                        .uri("lb://product-service"))

                // ---------- Inventory ----------
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("inventory-serviceCB")
                                        .setFallbackUri("forward:/fallback/inventory-service")))
                        .uri("lb://inventory-service"))

                // ---------- Cart + Orders ----------
                .route("order-service", r -> r
                        .path("/api/cart/**", "/api/orders/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("order-serviceCB")
                                        .setFallbackUri("forward:/fallback/order-service")))
                        .uri("lb://order-service"))

                // ---------- Payments + Payouts + Webhook ----------
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/payouts/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("payment-serviceCB")
                                        .setFallbackUri("forward:/fallback/payment-service")))
                        .uri("lb://payment-service"))

                // ---------- Notifications ----------
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("notification-serviceCB")
                                        .setFallbackUri("forward:/fallback/notification-service")))
                        .uri("lb://notification-service"))

                .build();
    }
}
