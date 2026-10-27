package org.fzl.incidentmanagement.service;
import org.fzl.incidentmanagement.dto.request.CommentRequest;
import org.fzl.incidentmanagement.dto.request.IncidentRequest;
import org.fzl.incidentmanagement.dto.response.*;
import org.fzl.incidentmanagement.entity.*;
import org.fzl.incidentmanagement.entity.enums.Role;
import org.fzl.incidentmanagement.entity.enums.Status;
import org.fzl.incidentmanagement.exception.BadRequestException;
import org.fzl.incidentmanagement.exception.ResourceNotFoundException;
import org.fzl.incidentmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final IncidentHistoryRepository historyRepository;
    private final IncidentCommentRepository commentRepository;

    @Transactional
    public IncidentResponse createIncident(IncidentRequest request, String email) {
        User creator = getUserByEmail(email);

        Incident incident = Incident.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(Status.OPEN)
                .createdBy(creator)
                .build();

        Incident savedIncident = incidentRepository.save(incident);

        // Ajouter à l'historique (Section 20)
        addHistory(savedIncident, "Incident créé");

        return mapToResponse(savedIncident);
    }

    public List<IncidentResponse> getMyIncidents(String email) {
        return incidentRepository.findByCreatedByEmailOrderByCreatedAtDesc(email)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<IncidentResponse> getAssignedIncidents(String email) {
        return incidentRepository.findByAssignedToEmailOrderByCreatedAtDesc(email)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<IncidentResponse> getAllIncidents() {
        return incidentRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public IncidentResponse getIncidentById(Long id, String email) {
        Incident incident = getIncident(id);
        User currentUser = getUserByEmail(email);

        // --- CONTRÔLE DE SÉCURITÉ (Section 12) ---
        boolean isOwner = incident.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAssignedTech = incident.getAssignedTo() != null &&
                incident.getAssignedTo().getId().equals(currentUser.getId());

        if (!isOwner && !isAdmin && !isAssignedTech) {
            throw new AccessDeniedException("Vous n'avez pas le droit de consulter cet incident.");
        }

        return mapToResponse(incident);
    }

    @Transactional
    public IncidentResponse updateStatus(Long id, Status newStatus, String email) {
        Incident incident = getIncident(id);
        User currentUser = getUserByEmail(email);

        // Règle métier (Section 11.8) : Un incident CLOSED ne peut plus être modifié
        if (incident.getStatus() == Status.CLOSED) {
            throw new BadRequestException("Impossible de modifier le statut d'un incident clôturé.");
        }

        // Vérifier que le technicien est bien celui affecté (Section 8.2)
        if (currentUser.getRole() == Role.TECHNICIAN) {
            if (incident.getAssignedTo() == null || !incident.getAssignedTo().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Vous ne pouvez modifier que les incidents qui vous sont affectés.");
            }
        }

        String oldStatus = incident.getStatus().name();
        incident.setStatus(newStatus);
        Incident updated = incidentRepository.save(incident);

        addHistory(updated, "Statut modifié : " + oldStatus + " -> " + newStatus.name());
        return mapToResponse(updated);
    }

    @Transactional
    public IncidentResponse assignIncident(Long id, Long technicianId) {
        Incident incident = getIncident(id);
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technicien introuvable."));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new BadRequestException("L'utilisateur ciblé n'est pas un technicien.");
        }

        incident.setAssignedTo(technician);
        incident.setStatus(Status.ASSIGNED); // Le statut passe automatiquement à ASSIGNED
        Incident updated = incidentRepository.save(incident);

        addHistory(updated, "Affecté au technicien : " + technician.getFullName());
        return mapToResponse(updated);
    }

    @Transactional
    public IncidentCommentResponse addComment(Long id, CommentRequest request, String email) {
        Incident incident = getIncident(id);
        User technician = getUserByEmail(email);

        // Vérifier que le technicien est bien affecté à cet incident
        if (incident.getAssignedTo() == null || !incident.getAssignedTo().getId().equals(technician.getId())) {
            throw new AccessDeniedException("Vous ne pouvez commenter que les incidents qui vous sont affectés.");
        }

        IncidentComment comment = IncidentComment.builder()
                .content(request.getContent())
                .incident(incident)
                .author(technician)
                .build();

        IncidentComment savedComment = commentRepository.save(comment);
        addHistory(incident, "Nouveau commentaire ajouté par " + technician.getFullName());

        return IncidentCommentResponse.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .authorName(technician.getFullName())
                .build();
    }

    public List<IncidentHistoryResponse> getIncidentHistory(Long id, String email) {
        // Réutilise la méthode getIncidentById pour vérifier les droits d'accès
        getIncidentById(id, email);

        return historyRepository.findByIncidentIdOrderByTimestampAsc(id)
                .stream().map(h -> IncidentHistoryResponse.builder()
                        .id(h.getId())
                        .action(h.getAction())
                        .timestamp(h.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    // --- MÉTHODES UTILITAIRES ---

    private Incident getIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident introuvable."));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private void addHistory(Incident incident, String action) {
        IncidentHistory history = IncidentHistory.builder()
                .incident(incident)
                .action(action)
                .build();
        historyRepository.save(history);
    }

    private IncidentResponse mapToResponse(Incident incident) {
        return IncidentResponse.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .category(incident.getCategory())
                .priority(incident.getPriority())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .createdBy(UserSummaryResponse.builder()
                        .id(incident.getCreatedBy().getId())
                        .fullName(incident.getCreatedBy().getFullName())
                        .build())
                .assignedTo(incident.getAssignedTo() != null ? UserSummaryResponse.builder()
                        .id(incident.getAssignedTo().getId())
                        .fullName(incident.getAssignedTo().getFullName())
                        .build() : null)
                .build();
    }
}