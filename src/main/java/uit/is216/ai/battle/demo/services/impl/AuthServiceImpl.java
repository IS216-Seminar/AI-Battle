package uit.is216.ai.battle.demo.services.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import uit.is216.ai.battle.demo.dtos.LoginRequest;
import uit.is216.ai.battle.demo.dtos.LoginResponse;
import uit.is216.ai.battle.demo.dtos.SignupRequest;
import uit.is216.ai.battle.demo.dtos.SignupResponse;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.UserRepository;
import uit.is216.ai.battle.demo.services.AuthService;
import uit.is216.ai.battle.demo.services.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public SignupResponse signup(SignupRequest request) throws IllegalArgumentException {
        // Validate password match
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create and save user
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new SignupResponse(accessToken, refreshToken);
    }

    @Override
    public LoginResponse login(LoginRequest request) throws IllegalArgumentException {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }
}
