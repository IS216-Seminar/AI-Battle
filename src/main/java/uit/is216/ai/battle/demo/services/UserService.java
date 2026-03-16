package uit.is216.ai.battle.demo.services;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uit.is216.ai.battle.demo.dtos.UserProfileResponse;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) authentication.getCredentials();
        UUID userId = jwtService.getUserIdFromToken(token);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(user.getId().toString(), user.getFullName(), user.getEmail(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}