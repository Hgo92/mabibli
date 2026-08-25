package com.hugo.mabibli.controller;

import com.hugo.mabibli.dto.AddLibraryRequest;
import com.hugo.mabibli.dto.LibraryResponse;
import com.hugo.mabibli.dto.UpdateLibraryRequest;
import com.hugo.mabibli.security.UserPrincipal;
import com.hugo.mabibli.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libraries")
public class LibraryController {
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public List<LibraryResponse> findAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return libraryService.findAll(principal.getUser());
    }

    @GetMapping("/{libraryId}")
    public LibraryResponse findOne(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId
    ) {
        return libraryService.findOne(principal.getUser(), libraryId);
    }

    @PostMapping
    public ResponseEntity<LibraryResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddLibraryRequest request
    ) {
        LibraryResponse response = libraryService.create(principal.getUser(), request);
        URI location = URI.create("/api/libraries/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{libraryId}")
    public LibraryResponse update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId,
            @Valid @RequestBody UpdateLibraryRequest request
    ) {
        return libraryService.update(
                principal.getUser(),
                libraryId,
                request
        );
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId
    ) {
        libraryService.delete(principal.getUser(), libraryId);
        return ResponseEntity.noContent().build();
    }
}
