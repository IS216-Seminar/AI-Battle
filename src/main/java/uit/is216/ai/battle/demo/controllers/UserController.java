package uit.is216.ai.battle.demo.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import uit.is216.ai.battle.demo.dtos.UserProfileResponse;
import uit.is216.ai.battle.demo.services.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get User Profile", description = "Retrieve the authenticated user's profile information")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token", content = @Content) })
    public ResponseEntity<UserProfileResponse> getProfile() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = (UUID) principal;
            UserProfileResponse profile = userService.getUserProfile(userId);
            return ResponseEntity.status(HttpStatus.OK).body(profile);
        } catch (IllegalArgumentException | ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
