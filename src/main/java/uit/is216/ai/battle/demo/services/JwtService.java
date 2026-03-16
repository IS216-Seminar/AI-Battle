package uit.is216.ai.battle.demo.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import uit.is216.ai.battle.demo.entities.User;

@Service
public class JwtService {

    @Value("${jwt.secret:defaultSecretKey}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes

    public String generateAccessToken(User user) {
        return Jwts.builder().setSubject(user.getId().toString()).claim("email", user.getEmail())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}