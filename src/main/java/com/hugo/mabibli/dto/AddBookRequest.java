package com.hugo.mabibli.dto;

import com.hugo.mabibli.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddBookRequest(
        @NotBlank String openLibraryId,
        @NotBlank String title,
        @NotBlank String author,
        String isbn,
        String cover,
        @NotBlank Long libraryId,
        Status status
) {}
