package uit.is216.ai.battle.demo.services;

import uit.is216.ai.battle.demo.dtos.LoginRequest;
import uit.is216.ai.battle.demo.dtos.LoginResponse;
import uit.is216.ai.battle.demo.dtos.SignupRequest;
import uit.is216.ai.battle.demo.dtos.SignupResponse;

public interface AuthService {
    SignupResponse signup(SignupRequest request) throws IllegalArgumentException;

    LoginResponse login(LoginRequest request) throws IllegalArgumentException;
}
