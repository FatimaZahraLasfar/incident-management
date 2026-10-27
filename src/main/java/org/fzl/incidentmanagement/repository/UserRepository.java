package org.fzl.incidentmanagement.repository;
import org.fzl.incidentmanagement.entity.User;
import org.fzl.incidentmanagement.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Utilisé par Spring Security pour charger l'utilisateur lors du login
    Optional<User> findByEmail(String email);

    // Utilisé lors de l'inscription pour vérifier si l'email est déjà pris (Section 11.2)
    Boolean existsByEmail(String email);

    // Utilisé pour les statistiques du Dashboard Admin (Section 19.3)
    long countByRole(Role role);
}