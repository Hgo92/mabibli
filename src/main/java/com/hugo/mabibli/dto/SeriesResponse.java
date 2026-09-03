package com.hugo.mabibli.dto;

import java.time.LocalDate;

public record SeriesResponse (
        Long id, String title, LocalDate createdAt, LocalDate updatedAt
){
}
