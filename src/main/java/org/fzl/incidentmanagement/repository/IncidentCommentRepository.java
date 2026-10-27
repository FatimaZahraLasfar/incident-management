package org.fzl.incidentmanagement.repository;

import org.fzl.incidentmanagement.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {

    // Récupérer tous les commentaires d'un incident, triés du plus ancien au plus récent
    List<IncidentComment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);
}
