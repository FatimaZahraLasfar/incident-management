package org.fzl.incidentmanagement.controller;
import org.fzl.incidentmanagement.dto.request.CommentRequest;
import org.fzl.incidentmanagement.dto.request.IncidentRequest;
import org.fzl.incidentmanagement.dto.request.StatusUpdateRequest;
import org.fzl.incidentmanagement.dto.response.IncidentCommentResponse;
import org.fzl.incidentmanagement.dto.response.IncidentHistoryResponse;
import org.fzl.incidentmanagement.dto.response.IncidentResponse;
import org.fzl.incidentmanagement.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    // --- ROUTES EMPLOYEE ---

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(incidentService.createIncident(request, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<IncidentResponse>> getMyIncidents(Authentication authentication) {
        return ResponseEntity.ok(incidentService.getMyIncidents(authentication.getName()));
    }

    // --- ROUTES TECHNICIAN ---

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<IncidentResponse>> getAssignedIncidents(Authentication authentication) {
        return ResponseEntity.ok(incidentService.getAssignedIncidents(authentication.getName()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(incidentService.updateStatus(id, request.getStatus(), authentication.getName()));
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<IncidentCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(incidentService.addComment(id, request, authentication.getName()), HttpStatus.CREATED);
    }

    // --- ROUTES ADMIN ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @PutMapping("/{id}/assign/{technicianId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> assignIncident(
            @PathVariable Long id,
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(incidentService.assignIncident(id, technicianId));
    }

    // --- ROUTES COMMUNES (Propriétaire, Technicien affecté ou Admin) ---

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(
            @PathVariable Long id,
            Authentication authentication) {
        // La sécurité (vérification de propriété) est gérée dans le Service
        return ResponseEntity.ok(incidentService.getIncidentById(id, authentication.getName()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<IncidentHistoryResponse>> getIncidentHistory(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(incidentService.getIncidentHistory(id, authentication.getName()));
    }
}