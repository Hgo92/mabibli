package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.AddBookRequest;
import com.hugo.mabibli.dto.AssignSeriesRequest;
import com.hugo.mabibli.dto.BookResponse;
import com.hugo.mabibli.dto.UpdateBookRequest;
import com.hugo.mabibli.entity.*;
import com.hugo.mabibli.exception.BookAlreadyExistsException;
import com.hugo.mabibli.exception.BookNotFoundException;
import com.hugo.mabibli.exception.LibraryNotFoundException;
import com.hugo.mabibli.exception.SeriesNotFoundException;
import com.hugo.mabibli.repository.BookRepository;
import com.hugo.mabibli.repository.LibraryRepository;
import com.hugo.mabibli.repository.SeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;
    private final SeriesRepository seriesRepository;

    public BookService(BookRepository bookRepository, LibraryRepository libraryRepository, SeriesRepository seriesRepository) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
        this.seriesRepository = seriesRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll(User user, Long libraryId) {
        libraryRepository
                .findByIdAndUser_Id(libraryId, user.getId())
                .orElseThrow(LibraryNotFoundException::new);

        return bookRepository
                .findAllByLibrary_IdAndLibrary_User_IdOrderByTitleAsc(
                        libraryId,
                        user.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findOne(User user, Long libraryId, Long bookId) {
        Book book = bookRepository
                .findByIdAndLibrary_IdAndLibrary_User_Id(
                        bookId,
                        libraryId,
                        user.getId()
                )
                .orElseThrow(BookNotFoundException::new);

        return toResponse(book);
    }

    public BookResponse addBook(User user, Long libraryId, AddBookRequest request) {
        Library library = libraryRepository
                .findByIdAndUser_Id(libraryId, user.getId())
                .orElseThrow(LibraryNotFoundException::new);

        if (bookRepository.existsByOpenLibraryIdAndLibrary_Id(request.openLibraryId(), library.getId())) {
            throw new BookAlreadyExistsException();
        }

        Book book = new Book();
        book.setOpenLibraryId(request.openLibraryId());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setDescription(request.description() != null ? request.description() : null);
        book.setIsbn(request.isbn());
        book.setCover(request.cover());
        book.setStatus(request.status() != null ? request.status() : Status.A_LIRE);
        book.setLibrary(library);
        book.setCreatedAt(LocalDate.now());

        bookRepository.save(book);
        return toResponse(book);
    }

    public BookResponse updateBook(User user, Long libraryId, Long bookId, UpdateBookRequest request) {
        Book book = bookRepository
                .findByIdAndLibrary_IdAndLibrary_User_Id(
                bookId,
                libraryId,
                user.getId())
                .orElseThrow(BookNotFoundException::new);

        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setStatus(request.status());
        book.setReadingDate(request.readingDate());
        book.setDescription(request.description());
        book.setCover(request.cover());
        book.setPages(request.pages());
        book.setCategories(new HashSet<>(request.categories()));
        book.setUpdatedAt(LocalDate.now());

        return toResponse(bookRepository.save(book));
    }

    public void deleteBook(User user, Long libraryId, Long bookId) {
        Book book = bookRepository
                .findByIdAndLibrary_IdAndLibrary_User_Id(
                        bookId,
                        libraryId,
                        user.getId()
                )
                .orElseThrow(BookNotFoundException::new);

        bookRepository.delete(book);
    }

    public BookResponse assignSeries(User user, Long libraryId, Long bookId, AssignSeriesRequest request) {
        Book book = bookRepository
                .findByIdAndLibrary_IdAndLibrary_User_Id(bookId, libraryId, user.getId())
                .orElseThrow(BookNotFoundException::new);

        Series series = seriesRepository
                .findByIdAndUser_Id(request.seriesId(), user.getId())
                .orElseThrow(SeriesNotFoundException::new);

        book.setSeries(series);
        book.setSeriesIndex(request.seriesIndex());
        book.setUpdatedAt(LocalDate.now());

        return toResponse(book);
    }

    public BookResponse removeSeries(User user, Long libraryId, Long bookId) {
        Book book = bookRepository
                .findByIdAndLibrary_IdAndLibrary_User_Id(bookId, libraryId, user.getId())
        .orElseThrow(BookNotFoundException::new);

        book.setSeries(null);
        book.setSeriesIndex(null);
        book.setUpdatedAt(LocalDate.now());

        return toResponse(book);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getLibrary().getId(),
                book.getOpenLibraryId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getStatus(),
                book.getReadingDate(),
                book.getDescription(),
                book.getCover(),
                book.getPages(),
                book.getSeries() != null ? book.getSeries().getId() : null,
                book.getSeries() != null ? book.getSeries().getTitle() : null,
                book.getSeriesIndex(),
                Set.copyOf(book.getCategories()),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
