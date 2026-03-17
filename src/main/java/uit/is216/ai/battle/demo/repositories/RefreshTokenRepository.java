package uit.is216.ai.battle.demo.repositories;

import java.util.UUID;
import uit.is216.ai.battle.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uit.is216.ai.battle.demo.entities.RefreshToken; // Đảm bảo đúng path tới entity RefreshToken
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    // Tìm token trong DB để kiể
    Optional<RefreshToken> findByToken(String token);
}
