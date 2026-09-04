package com.hugo.mabibli.dto;

import com.hugo.mabibli.entity.Category;
import com.hugo.mabibli.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record UpdateBookRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
        String title,

        @NotBlank(message = "L'auteur est obligatoire")
        @Size(max = 255, message = "L'auteur ne peut pas dépasser 255 caractères")
        String author,

        @NotNull(message = "Le statut est obligatoire")
        Status status,

        LocalDate readingDate,

        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
        String description,

        @Size(max = 500, message = "L'URL de couverture ne peut pas dépasser 500 caractères")
        String cover,

        @Positive(message = "Le nombre de pages doit être positif")
        Integer pages,

        @NotNull(message = "La liste des catégories est obligatoire")
        Set<Category> categories
) {
}