package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class IncidentCommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String authorName; // Seulement le nom du technicien
}