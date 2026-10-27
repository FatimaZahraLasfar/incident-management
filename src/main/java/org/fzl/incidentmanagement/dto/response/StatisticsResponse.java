package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse {
    // Pour l'admin
    private long totalUsers;
    private long totalEmployees;
    private long totalTechnicians;

    // Pour les incidents (global ou filtré selon le rôle)
    private long totalIncidents;
    private long openIncidents;
    private long inProgressIncidents;
    private long resolvedIncidents;
    private long closedIncidents;
}