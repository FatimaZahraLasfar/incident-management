package org.fzl.incidentmanagement.repository;

import org.fzl.incidentmanagement.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long> {

    // Récupérer l'historique d'un incident, trié du plus ancien au plus récent
    List<IncidentHistory> findByIncidentIdOrderByTimestampAsc(Long incidentId);
}