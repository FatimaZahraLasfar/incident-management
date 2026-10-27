package org.fzl.incidentmanagement.service;

import org.fzl.incidentmanagement.dto.request.PasswordChangeRequest;
import org.fzl.incidentmanagement.dto.request.ProfileUpdateRequest;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.entity.User;
import org.fzl.incidentmanagement.exception.BadRequestException;
import org.fzl.incidentmanagement.exception.ResourceNotFoundException;
import org.fzl.incidentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // <--- INDISPENSABLE : Dit à Spring de gérer cette classe
@RequiredArgsConstructor // <--- Génère le constructeur pour injecter les dépendances
public class UserService {

    // Dépendances injectées automatiquement par Spring
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(String email) {
        User user = getUserByEmail(email);
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = getUserByEmail(email);

        // Vérifier si le nouvel email n'est pas déjà pris par quelqu'un d'autre
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé par un autre compte.");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        return mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        User user = getUserByEmail(email);

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect.");
        }

        // Vérifier la correspondance des nouveaux mots de passe
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Le nouveau mot de passe et sa confirmation ne correspondent pas.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Méthode utilitaire réutilisable
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
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