package org.fzl.incidentmanagement.controller;

import org.fzl.incidentmanagement.dto.request.PasswordChangeRequest;
import org.fzl.incidentmanagement.dto.request.ProfileUpdateRequest;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès.");
    }
}