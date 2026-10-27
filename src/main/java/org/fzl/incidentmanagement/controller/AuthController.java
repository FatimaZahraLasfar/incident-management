package org.fzl.incidentmanagement.controller;

import org.fzl.incidentmanagement.dto.request.LoginRequest;
import org.fzl.incidentmanagement.dto.request.RegisterRequest;
import org.fzl.incidentmanagement.dto.response.AuthResponse;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
