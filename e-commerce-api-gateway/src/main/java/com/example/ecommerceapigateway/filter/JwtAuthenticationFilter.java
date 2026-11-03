package com.example.ecommerceapigateway.filter;




import com.example.ecommerceapigateway.security.JwtProperties;
import com.example.ecommerceapigateway.security.JwtService;
import com.example.ecommerceapigateway.security.PublicEndpoints;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Validates JWT on every non-public request.
 * On success: injects X-User-Id, X-User-Roles, X-User-Email headers so downstream
 * services don't need to re-parse the token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final JwtProperties props;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Let public paths pass through untouched
        if (PublicEndpoints.isPublic(path)) {
            return chain.filter(exchange);
        }

        // 2. Extract the token
        String authHeader = exchange.getRequest().getHeaders().getFirst(props.getHeaderName());
        if (authHeader == null || !authHeader.startsWith(props.getHeaderPrefix())) {
            return unauthorized(exchange, "Missing or malformed Authorization header");
        }
        String token = authHeader.substring(props.getHeaderPrefix().length()).trim();

        // 3. Validate
        Claims claims = jwtService.parse(token);
        if (claims == null) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        // 4. Inject user context headers for downstream services
        String userId = String.valueOf(claims.get("userId"));
        String email = (String) claims.get("email");
        List<?> roles = claims.get("roles", List.class);

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-User-Email", email != null ? email : "")
                .header("X-User-Roles", roles != null ? String.join(",", toStringList(roles)) : "")
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private List<String> toStringList(List<?> raw) {
        return raw.stream().map(String::valueOf).toList();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = """
                {"error":"UNAUTHORIZED","message":"%s"}
                """.formatted(message);
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
