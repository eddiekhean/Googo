package com.example.userservice.util;

import com.example.userservice.dto.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Getter
    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtil(
            @Value("${jwt.private-key-path}") String privateKeyPath,
            @Value("${jwt.public-key-path}") String publicKeyPath
    ) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        var file = ResourceUtils.getFile(path);
        String key = new String(Files.readAllBytes(file.toPath()))
                .replaceAll("-----BEGIN ([A-Z ]+)-----", "")
                .replaceAll("-----END ([A-Z ]+)-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
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
    // --- Sinh Access Token ---
    public String generateAccessToken(UserDetailsImpl user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("userName", user.getUsername())
                .claim("role", user.getAuthorities().stream().findFirst())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // --- Sinh Refresh Token ---
    public String generateRefreshToken(UserDetailsImpl user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getAuthorities().stream().findFirst())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
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
            extractClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
