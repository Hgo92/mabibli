package com.hugo.mabibli.dto;

import java.time.LocalDate;

public record LibraryResponse(
        Long id,
        String title,
        LocalDate createdAt,
        LocalDate updatedAt
) {
}
