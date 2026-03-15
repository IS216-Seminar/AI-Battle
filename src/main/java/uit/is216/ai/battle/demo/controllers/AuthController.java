package uit.is216.ai.battle.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import uit.is216.ai.battle.demo.dtos.LoginRequest;
import uit.is216.ai.battle.demo.dtos.LoginResponse;
import uit.is216.ai.battle.demo.dtos.SignupRequest;
import uit.is216.ai.battle.demo.dtos.SignupResponse;
import uit.is216.ai.battle.demo.services.AuthService;

@RestController
@RequestMapping("/public/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for user signup and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "User Signup", description = "Register a new user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request - passwords do not match", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict - email already registered", content = @Content) })
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user with email and password, returns access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content) })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}