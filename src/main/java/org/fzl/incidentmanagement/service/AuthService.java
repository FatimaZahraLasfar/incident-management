package org.fzl.incidentmanagement.service;
import org.fzl.incidentmanagement.dto.request.LoginRequest;
import org.fzl.incidentmanagement.dto.request.RegisterRequest;
import org.fzl.incidentmanagement.dto.response.AuthResponse;
import org.fzl.incidentmanagement.dto.response.UserResponse;
import org.fzl.incidentmanagement.entity.User;
import org.fzl.incidentmanagement.entity.enums.Role;
import org.fzl.incidentmanagement.exception.BadRequestException;
import org.fzl.incidentmanagement.exception.ResourceNotFoundException;
import org.fzl.incidentmanagement.repository.UserRepository;
import org.fzl.incidentmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà (Section 14)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé.");
        }

        // Créer le nouvel utilisateur (Section 11.1 & 11.3)
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt
                .role(Role.EMPLOYEE) // Rôle forcé, pas de choix possible
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // Authentification via Spring Security (vérifie email, mot de passe et si le compte est enabled)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si on arrive ici, l'authentification a réussi
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Génération du token JWT
        String jwtToken = jwtService.generateToken(user, user.getRole().name());

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .fullName(user.getFullName())
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