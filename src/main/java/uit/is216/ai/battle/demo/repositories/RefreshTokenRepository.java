package uit.is216.ai.battle.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uit.is216.ai.battle.demo.entities.RefreshToken;
import uit.is216.ai.battle.demo.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
