package org.fzl.incidentmanagement.repository;
import org.fzl.incidentmanagement.entity.Incident;
import org.fzl.incidentmanagement.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // --- POUR L'EMPLOYEE (Section 8.1 & 10.2) ---
    // Récupérer les incidents créés par un utilisateur spécifique (via son email)
    List<Incident> findByCreatedByEmailOrderByCreatedAtDesc(String email);

    // Compter les incidents d'un employé selon leur statut (Pour le Dashboard 19.1)
    long countByCreatedByEmail(String email);
    long countByCreatedByEmailAndStatus(String email, Status status);


    // --- POUR LE TECHNICIAN (Section 8.2 & 10.2) ---
    // Récupérer les incidents affectés à un technicien spécifique
    List<Incident> findByAssignedToEmailOrderByCreatedAtDesc(String email);

    // Compter les incidents d'un technicien selon leur statut/priorité (Pour le Dashboard 19.2)
    long countByAssignedToEmail(String email);
    long countByAssignedToEmailAndStatus(String email, Status status);
    long countByAssignedToEmailAndPriority(String email, org.fzl.incidentmanagement.entity.enums.Priority priority);


    // --- POUR L'ADMIN (Section 19.3) ---
    // Compter tous les incidents selon leur statut pour les statistiques globales
    long countByStatus(Status status);
}
