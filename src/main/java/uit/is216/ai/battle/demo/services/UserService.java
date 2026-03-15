package uit.is216.ai.battle.demo.services;

import uit.is216.ai.battle.demo.dtos.UserProfileResponse;

import java.util.UUID;

/**
 * User Service Interface Handles user profile operations
 */
public interface UserService {
    /**
     * Get user profile information
     *
     * @param userId
     *            the user's UUID
     *
     * @return the user's profile information
     *
     * @throws IllegalArgumentException
     *             if user not found
     */
    UserProfileResponse getUserProfile(UUID userId) throws IllegalArgumentException;
}
