package uit.is216.ai.battle.demo.services;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(UUID userId, String email);

    String generateRefreshToken(UUID userId);

    UUID extractUserId(String token);

    String extractEmail(String token);

    String extractTokenType(String token);

    boolean isTokenValid(String token);
}
