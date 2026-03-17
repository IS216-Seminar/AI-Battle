package uit.is216.ai.battle.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Đảm bảo đúng path tới entity RefreshToken

import uit.is216.ai.battle.demo.entities.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    // Tìm token trong DB để kiể
    Optional<RefreshToken> findByToken(String token);
}
