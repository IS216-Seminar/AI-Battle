package uit.is216.ai.battle.demo.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uit.is216.ai.battle.demo.dtos.LoginRequest;
import uit.is216.ai.battle.demo.dtos.LoginResponse;
import uit.is216.ai.battle.demo.dtos.SignupRequest;
import uit.is216.ai.battle.demo.entities.RefreshToken;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.RefreshTokenRepository;
import uit.is216.ai.battle.demo.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void signup(SignupRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder().fullName(request.fullName()).email(request.email())
                .password(passwordEncoder.encode(request.password())).build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    private String generateRefreshToken(User user) {
        // Clean up old tokens for user
        refreshTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        RefreshToken refreshTokenEntity = RefreshToken.builder().token(token).userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7)).build();

        refreshTokenRepository.save(refreshTokenEntity);
        return token;
    }
}