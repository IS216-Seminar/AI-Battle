package uit.is216.ai.battle.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for user signup")
public record SignupRequest(@Schema(description = "User's full name", example = "Nguyen Van A") String fullName,

        @Schema(description = "User's email address", example = "a@example.com") String email,

        @Schema(description = "User's password", example = "secret123") String password,

        @Schema(description = "Password confirmation", example = "secret123") String confirmPassword) {
}
