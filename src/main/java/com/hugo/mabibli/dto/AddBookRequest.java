package com.hugo.mabibli.dto;

import com.hugo.mabibli.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddBookRequest(
        @NotBlank @Size(max = 50) String openLibraryId,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String author,
        @Size(max = 25) String isbn,
        @Size(max = 500) String cover,
        Status status,
        @Size(max=1000) String description
) {}
