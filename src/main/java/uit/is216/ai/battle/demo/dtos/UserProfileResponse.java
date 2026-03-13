package uit.is216.ai.battle.demo.dtos;

import java.time.LocalDateTime;

public record UserProfileResponse(String id, String fullName, String email, LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
