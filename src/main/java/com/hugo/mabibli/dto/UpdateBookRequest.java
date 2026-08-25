package com.hugo.mabibli.dto;

import com.hugo.mabibli.entity.Category;
import com.hugo.mabibli.entity.Status;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record UpdateBookRequest(
        @Size(max = 255)String title,
        @Size(max = 255) String author,
        Status status,
        LocalDate readingDate,
        @Size(max=1000) String description,
        @Size(max = 500)  String cover,
        @Positive Integer pages,
        @Positive Integer seriesIndex,
        Set<Category> categories
) {}
