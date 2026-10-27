package org.fzl.incidentmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.fzl.incidentmanagement.entity.enums.Status;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Le nouveau statut est obligatoire")
    private Status status;
}
