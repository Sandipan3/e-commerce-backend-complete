package com.example.ecommerceapigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Validates JWTs issued by user-service.
 * The gateway does NOT issue tokens — it only verifies them.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;
    private volatile SecretKey key;

    private SecretKey key() {
        if (key == null) {
            synchronized (this) {
                if (key == null) {
                    key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        return key;
    }

    /**
     * Parse and verify the token. Returns claims if valid, null otherwise.
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            log.debug("JWT parse failed: {}", ex.getMessage());
            return null;
        }
    }

    public boolean isValid(String token) {
        Claims c = parse(token);
        if (c == null) return false;
        return c.getExpiration() == null || c.getExpiration().after(new Date());
    }
}