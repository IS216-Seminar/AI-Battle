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

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
}