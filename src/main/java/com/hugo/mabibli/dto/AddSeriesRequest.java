package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddSeriesRequest(
        @NotBlank(message="Le titre est obligatoire")
        @Size(max=255,message="Le titre ne peut pas dépasser 255 caractères")
        String title
) {
}
