package uit.is216.ai.battle.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body for successful signup with JWT tokens")
public record SignupResponse(
        @Schema(description = "Short-lived JWT access token (15 minutes)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,

        @Schema(description = "Long-lived refresh token (7 days) for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String refreshToken) {
}
