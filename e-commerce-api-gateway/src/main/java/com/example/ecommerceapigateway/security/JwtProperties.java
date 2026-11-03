package com.example.ecommerceapigateway.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HMAC secret — must match user-service.
     * Min 32 chars for HS256, 64 for HS512.
     */
    private String secret;

    /**
     * Comma-separated list of public paths that skip JWT validation.
     * Ant-style patterns.
     */
    private String publicPaths;

    /**
     * Header name that carries the JWT. Defaults to Authorization: Bearer <token>.
     */
    private String headerName = "Authorization";

    /**
     * Prefix before the token in the header. Default "Bearer ".
     */
    private String headerPrefix = "Bearer ";
}
