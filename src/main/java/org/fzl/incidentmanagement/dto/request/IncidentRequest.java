package org.fzl.incidentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.fzl.incidentmanagement.entity.enums.Category;
import org.fzl.incidentmanagement.entity.enums.Priority;

@Data
public class IncidentRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private Category category;

    @NotNull(message = "La priorité est obligatoire")
    private Priority priority;
}
