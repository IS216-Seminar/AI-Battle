package uit.is216.ai.battle.demo.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import uit.is216.ai.battle.demo.dtos.UserProfileResponse;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.UserRepository;
import uit.is216.ai.battle.demo.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileResponse getUserProfile(UUID userId) throws IllegalArgumentException {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResponse(user.getId().toString(), user.getFullName(), user.getEmail(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
