package uit.is216.ai.battle.demo.dtos;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User profile information response")
public record UserProfileResponse(
        @Schema(description = "User's unique identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000") String id,

        @Schema(description = "User's full name", example = "Nguyen Van A") String fullName,

        @Schema(description = "User's email address", example = "a@example.com") String email,

        @Schema(description = "Account creation timestamp", example = "2025-01-01T00:00:00") LocalDateTime createdAt,

        @Schema(description = "Last account update timestamp", example = "2025-01-01T00:00:00") LocalDateTime updatedAt) {
}
