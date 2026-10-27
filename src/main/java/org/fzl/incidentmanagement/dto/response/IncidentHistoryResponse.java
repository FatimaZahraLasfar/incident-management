package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class IncidentHistoryResponse {
    private Long id;
    private String action;
    private LocalDateTime timestamp;
}