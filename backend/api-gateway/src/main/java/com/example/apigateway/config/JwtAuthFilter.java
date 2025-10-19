package com.example.apigateway.config;

import com.example.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth",
            "/swagger",
            "/v3/api-docs",
            "/actuator",
            "/health"
    );

    private final JwtUtil jwtUtil;
    private final String internalSecret;

    public JwtAuthFilter(JwtUtil jwtUtil, @Value("${security.internal.secret}") String internalSecret) {
        this.jwtUtil = jwtUtil;
        this.internalSecret = internalSecret;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println(">>> [GATEWAY] Incoming path: " + path);

        if (isPublicPath(path)) {
            System.out.println(">>> [GATEWAY] Public path detected, skipping JWT check.");
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        String userName = claims.get("userName", String.class);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = hmacSha256(userId + ":" + role + ":" + timestamp, internalSecret);

        ServerHttpRequest mutated = request.mutate()
                .header("X-User-Id", userId)
                .header("X-Role", role)
                .header("X-User-Name", userName)
                .header("X-Timestamp", timestamp)
                .header("X-Internal-Signature", signature)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }
        String bearer = authHeaders.get(0);
        if (!bearer.startsWith("Bearer ")) {
            return null;
        }
        return bearer.substring(7);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }
}
