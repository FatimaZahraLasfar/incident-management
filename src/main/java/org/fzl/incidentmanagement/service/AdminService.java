package org.fzl.incidentmanagement.service;
import org.fzl.incidentmanagement.dto.response.StatisticsResponse;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.entity.User;
import org.fzl.incidentmanagement.entity.enums.Priority;
import org.fzl.incidentmanagement.entity.enums.Role;
import org.fzl.incidentmanagement.entity.enums.Status;
import org.fzl.incidentmanagement.exception.ResourceNotFoundException;
import org.fzl.incidentmanagement.repository.IncidentRepository;
import org.fzl.incidentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id, boolean enable) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        user.setEnabled(enable);
        return mapToUserResponse(userRepository.save(user));
    }

    public StatisticsResponse getGlobalStatistics() {
        return StatisticsResponse.builder()
                // Stats Utilisateurs
                .totalUsers(userRepository.count())
                .totalEmployees(userRepository.countByRole(Role.EMPLOYEE))
                .totalTechnicians(userRepository.countByRole(Role.TECHNICIAN))
                // Stats Incidents
                .totalIncidents(incidentRepository.count())
                .openIncidents(incidentRepository.countByStatus(Status.OPEN))
                .inProgressIncidents(incidentRepository.countByStatus(Status.IN_PROGRESS))
                .resolvedIncidents(incidentRepository.countByStatus(Status.RESOLVED))
                .closedIncidents(incidentRepository.countByStatus(Status.CLOSED))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}