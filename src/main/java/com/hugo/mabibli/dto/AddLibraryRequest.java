package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddLibraryRequest(
        @NotBlank(message="Le titre est obligatoire")
        @Size(max=100, message = "Le titre ne peut pas dépasser 100 caractères")
        String title
) {
}
