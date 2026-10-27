package org.fzl.incidentmanagement.controller;

import org.fzl.incidentmanagement.dto.response.StatisticsResponse;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Sécurise TOUTES les routes de ce contrôleur pour l'ADMIN
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id, true));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserStatus(id, false));
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminService.getGlobalStatistics());
    }
}