package com.hugo.mabibli.controller;

import com.hugo.mabibli.dto.*;
import com.hugo.mabibli.security.UserPrincipal;
import com.hugo.mabibli.service.BookService;
import com.hugo.mabibli.service.OpenLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libraries/{libraryId}/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> findAll(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long libraryId) {
        List<BookResponse> books = bookService.findAll(principal.getUser(), libraryId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> findOne(@AuthenticationPrincipal UserPrincipal principal,
                                                @PathVariable Long libraryId,
                                                @PathVariable Long bookId) {
        BookResponse book = bookService.findOne(principal.getUser(), libraryId, bookId);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(@AuthenticationPrincipal UserPrincipal principal,
                                                @PathVariable Long libraryId,
                                                @Valid @RequestBody AddBookRequest request) {
        BookResponse createdBook = bookService.addBook(principal.getUser(), libraryId, request);

        URI location = URI.create(
                "/api/libraries/" + libraryId + "/books/" + createdBook.id()
        );
        return ResponseEntity.created(location).body(createdBook);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId,
            @PathVariable Long bookId,
            @Valid @RequestBody UpdateBookRequest request) {
        BookResponse updatedBook = bookService.updateBook(principal.getUser(), libraryId, bookId, request);
        return ResponseEntity.ok(updatedBook);
    }

    @PutMapping("/{bookId}/series")
    public ResponseEntity<BookResponse> assignSeries(

            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId,
            @PathVariable Long bookId,
            @Valid @RequestBody AssignSeriesRequest request
    ) {
        return ResponseEntity.ok(
                bookService.assignSeries(
                        principal.getUser(),
                        libraryId,
                        bookId,
                        request
                )
        );
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId,
            @PathVariable Long bookId
    ) {
        bookService.deleteBook(
                principal.getUser(),
                libraryId,
                bookId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bookId}/series")
    public ResponseEntity<BookResponse> removeSeries(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long libraryId,
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(
                bookService.removeSeries(
                        principal.getUser(),
                        libraryId,
                        bookId
                )
        );
    }

}
