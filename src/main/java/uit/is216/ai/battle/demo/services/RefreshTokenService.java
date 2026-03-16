package uit.is216.ai.battle.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.is216.ai.battle.demo.entities.RefreshToken;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenDurationDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                              @Value("${jwt.refresh-token-days:7}") long refreshTokenDurationDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDurationDays = refreshTokenDurationDays;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenDurationDays));
        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
