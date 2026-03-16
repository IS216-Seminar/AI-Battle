package uit.is216.ai.battle.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uit.is216.ai.battle.demo.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
