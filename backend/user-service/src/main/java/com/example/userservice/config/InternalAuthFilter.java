package com.example.userservice.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Value("${security.internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy header do Gateway chèn vào
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-Role");
        String timestamp = request.getHeader("X-Timestamp");
        String signature = request.getHeader("X-Internal-Signature");

        // Nếu thiếu header → từ chối ngay
        if (userId == null || role == null || timestamp == null || signature == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing internal headers");
            return;
        }

        try {
            // Tạo lại HMAC từ data
            String data = userId + ":" + role + ":" + timestamp;
            String expectedSignature = hmacSha256(data, internalSecret);

            // Kiểm tra chữ ký
            if (!expectedSignature.equals(signature)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid internal signature");
                return;
            }

            // Optional: chặn request quá cũ (nếu timestamp > 1 phút)
            long now = System.currentTimeMillis();
            if (Math.abs(now - Long.parseLong(timestamp)) > 60_000) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Expired internal request");
                return;
            }

            // Nếu hợp lệ → set Authentication cho SecurityContext
            var auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Internal signature verification failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}