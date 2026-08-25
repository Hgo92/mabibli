package com.hugo.mabibli.controller;

import com.hugo.mabibli.dto.BookSearchResult;
import com.hugo.mabibli.service.OpenLibraryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/open-library")
public class OpenLibraryController {

    private final OpenLibraryService openLibraryService;

    public OpenLibraryController(
            OpenLibraryService openLibraryService
    ) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/search")
    public List<BookSearchResult> search(
            @RequestParam
            @NotBlank
            @Size(max = 100)
            String q
    ) {
        return openLibraryService.search(q.trim());
    }
}