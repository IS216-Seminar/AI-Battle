package uit.is216.ai.battle.demo.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil implements JwtService {

    @Value("${jwt.secret:your-secret-key-that-must-be-at-least-32-characters-long-for-hs256}")
    private String jwtSecret;

    @Value("${jwt.expiration:900000}") // 15 minutes default
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private long jwtRefreshExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UUID userId, String email) {
        return Jwts.builder().subject(userId.toString()).claim("email", email).claim("type", "access")
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey()).compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder().subject(userId.toString()).claim("type", "refresh").issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs)).signWith(getSigningKey())
                .compact();
    }

    @Override
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    @Override
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    @Override
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}
