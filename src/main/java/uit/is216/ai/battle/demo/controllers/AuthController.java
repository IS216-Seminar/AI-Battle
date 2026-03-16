package uit.is216.ai.battle.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uit.is216.ai.battle.demo.dtos.LoginRequest;
import uit.is216.ai.battle.demo.dtos.LoginResponse;
import uit.is216.ai.battle.demo.dtos.SignupRequest;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.services.UserService;
import uit.is216.ai.battle.demo.services.JwtService;
import uit.is216.ai.battle.demo.services.RefreshTokenService;

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userService.existsByEmail(request.email())) {
            throw new org.springframework.dao.DuplicateKeyException("Email already exists");
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(request.password())
                .build();
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                request.email(), request.password());
        authenticationManager.authenticate(auth);
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new java.util.NoSuchElementException("User not found"));
        String accessToken = jwtService.generateToken(user.getEmail(), java.util.Map.of("userId", user.getId().toString()));
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }
}