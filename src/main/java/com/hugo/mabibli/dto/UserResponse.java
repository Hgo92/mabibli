package com.hugo.mabibli.dto;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String username,
        LocalDate createdAt
) {
}