package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import org.fzl.incidentmanagement.entity.enums.Category;
import org.fzl.incidentmanagement.entity.enums.Priority;
import org.fzl.incidentmanagement.entity.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponse {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations réduites sur les utilisateurs liés (sans données sensibles)
    private UserSummaryResponse createdBy;
    private UserSummaryResponse assignedTo;
}
