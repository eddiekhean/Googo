package com.example.apigateway.util;

import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    @Getter
    @Value("${jwt.access-expiration}")
    private long accessExpiration;


    private final PublicKey publicKey;

    public JwtUtil(
            @Value("${jwt.public-key-path}") String publicKeyPath
    ) throws Exception {
        this.publicKey = loadPublicKey(publicKeyPath);
    }


    private PublicKey loadPublicKey(String path) throws Exception {
        var file = ResourceUtils.getFile(path);
        String key = new String(Files.readAllBytes(file.toPath()))
                .replaceAll("-----BEGIN ([A-Z ]+)-----", "")
                .replaceAll("-----END ([A-Z ]+)-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }


    // --- Verify token ---
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);

            // kiểm tra hết hạn
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                System.out.println(">>> [JWT] Token expired");
                return false;
            }

            // có thể thêm logic khác như userId, role...
            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                System.out.println(">>> [JWT] Missing subject in token");
                return false;
            }

            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println(">>> [JWT] Invalid signature or malformed token");
        } catch (ExpiredJwtException e) {
            System.out.println(">>> [JWT] Token expired");
        } catch (UnsupportedJwtException e) {
            System.out.println(">>> [JWT] Unsupported JWT");
        } catch (IllegalArgumentException e) {
            System.out.println(">>> [JWT] Illegal token argument");
        }
        return false;
    }

}
