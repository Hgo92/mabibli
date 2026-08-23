package com.hugo.mabibli.config;

import com.hugo.mabibli.dto.AddBookRequest;
import com.hugo.mabibli.dto.BookSearchResult;
import com.hugo.mabibli.security.UserPrincipal;
import com.hugo.mabibli.service.BookService;
import com.hugo.mabibli.service.OpenLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final OpenLibraryService openLibraryService;
    private final BookService bookService;

    public BookController(OpenLibraryService openLibraryService, BookService bookService) {
        this.openLibraryService = openLibraryService;
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookSearchResult>> search(@RequestParam String q) {
        return ResponseEntity.ok(openLibraryService.search(q));
    }

    @PostMapping
    public ResponseEntity<Void> addBook(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody AddBookRequest request) {
        bookService.addBook(principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
