package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSeriesRequest(
        @NotBlank(message="Le titre est obligatoire")
        @Size(max=255, message="Le titre ne doit pas dépasser 255 caractères")
        String title
) {
}
