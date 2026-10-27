package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {
    private Long id;
    private String fullName;
}