package org.fzl.incidentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Le contenu du commentaire ne peut pas être vide")
    private String content;
}