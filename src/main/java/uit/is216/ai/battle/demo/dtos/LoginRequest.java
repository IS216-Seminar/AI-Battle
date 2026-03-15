package uit.is216.ai.battle.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for user login")
public record LoginRequest(@Schema(description = "User's email address", example = "a@example.com") String email,

        @Schema(description = "User's password", example = "secret123") String password) {
}