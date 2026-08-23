package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.AddBookRequest;
import com.hugo.mabibli.entity.Book;
import com.hugo.mabibli.entity.Library;
import com.hugo.mabibli.entity.Status;
import com.hugo.mabibli.entity.User;
import com.hugo.mabibli.exception.BookAlreadyExistsException;
import com.hugo.mabibli.exception.LibraryNotFoundException;
import com.hugo.mabibli.repository.BookRepository;
import com.hugo.mabibli.repository.LibraryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;

    public BookService(BookRepository bookRepository, LibraryRepository libraryRepository) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
    }

    public void addBook(User user, AddBookRequest request) {
        Library library = libraryRepository.findById(request.libraryId())
                .filter(l -> l.getUser().getId().equals(user.getId()))
                .orElseThrow(LibraryNotFoundException::new);

        if (bookRepository.existsByOpenLibraryIdAndUserId(request.openLibraryId(), user.getId())) {
            throw new BookAlreadyExistsException();
        }

        Book book = new Book();
        book.setOpenLibraryId(request.openLibraryId());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setCover(request.cover());
        book.setStatus(request.status() != null ? request.status() : Status.A_LIRE);
        book.setUser(user);
        book.setLibrary(library);
        book.setCreatedAt(LocalDate.now());

        bookRepository.save(book);
    }

}
