package com.example.apigateway.config;

import com.example.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    private final String internalSecret = "super_strong_internal_secret_key";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println(">>> [GATEWAY] Incoming request path: " + path);

        // Bỏ qua public path
        if (path.startsWith("/api/v1/auth") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            System.out.println(">>> [GATEWAY] Public path detected, skipping JWT filter.");
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (token == null) {
            System.out.println(">>> [GATEWAY] Missing Authorization header.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        System.out.println(">>> [GATEWAY] Token received: " + token.substring(0, Math.min(20, token.length())) + "...");

        if (!jwtUtil.validateToken(token)) {
            System.out.println(">>> [GATEWAY] Invalid JWT token.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.extractClaims(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        String userName = claims.get("userName", String.class);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = hmacSha256(userId + ":" + role + ":" + timestamp, internalSecret);

        System.out.println(">>> [GATEWAY] Authenticated user: " + userName + " (" + role + ")");
        System.out.println(">>> [GATEWAY] Forwarding request with internal headers.");

        // Build request mới có header nội bộ
        ServerHttpRequest mutated = request.mutate()
                .header("X-User-Id", userId)
                .header("X-Role", role)
                .header("X-User-Name", userName)
                .header("X-Timestamp", timestamp)
                .header("X-Internal-Signature", signature)
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private String extractToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}
