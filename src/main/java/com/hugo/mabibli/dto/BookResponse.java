package com.hugo.mabibli.dto;

import com.hugo.mabibli.entity.Category;
import com.hugo.mabibli.entity.Status;

import java.time.LocalDate;
import java.util.Set;

public record BookResponse(
        Long id,
        Long libraryId,
        String openLibraryId,
        String isbn,
        String title,
        String author,
        Status status,
        LocalDate readingDate,
        String description,
        String cover,
        Integer pages,
        Long seriesId,
        String seriesTitle,
        Integer seriesIndex,
        Set<Category> categories,
        LocalDate createdAt,
        LocalDate updatedAt) {
}
