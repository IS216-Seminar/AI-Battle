package uit.is216.ai.battle.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uit.is216.ai.battle.demo.dtos.UserProfileResponse;
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.services.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new java.util.NoSuchElementException("User not found"));
        UserProfileResponse profile = new UserProfileResponse(
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
        return ResponseEntity.ok(profile);
    }
}
