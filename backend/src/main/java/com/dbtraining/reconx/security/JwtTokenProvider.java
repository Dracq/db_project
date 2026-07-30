package com.dbtraining.reconx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.Jwts;

/**
 * ============================================================================
 * TICKET-ADV072 — JWT Token Provider (io.jsonwebtoken:jjwt)
 *
 * WHAT:    Generates and validates JWTs for authenticated sessions.
 * HOW:     Uses standard JJWT builder/parser. Keys are HMAC-SHA-256 derived
 *          from the application.yml secret.
 * WHY:     Stateless auth allows backend scaling without sticky sessions or
 *          Redis session stores.
 * ============================================================================
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMinutes;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${reconx.security.jwt.secret}") String secret,
            @Value("${reconx.security.jwt.expiration-minutes}") long expirationMinutes,
            @Value("${reconx.security.jwt.issuer}") String issuer) {
        // Need at least 256 bits (32 bytes) for HS256
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long for HS256");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }

    public String generate(String email, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .subject(email)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of("role", role))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long expirationSeconds() { return expirationMinutes * 60; }
}
