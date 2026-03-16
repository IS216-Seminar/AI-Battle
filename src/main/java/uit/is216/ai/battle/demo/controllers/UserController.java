package uit.is216.ai.battle.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uit.is216.ai.battle.demo.dtos.UserProfileResponse;
import uit.is216.ai.battle.demo.services.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        UserProfileResponse response = userService.getProfile();
        return ResponseEntity.ok(response);
    }
}
