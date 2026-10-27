package org.fzl.incidentmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import org.fzl.incidentmanagement.entity.enums.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
