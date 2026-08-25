package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.AddLibraryRequest;
import com.hugo.mabibli.dto.LibraryResponse;
import com.hugo.mabibli.dto.UpdateLibraryRequest;
import com.hugo.mabibli.entity.Library;
import com.hugo.mabibli.entity.User;
import com.hugo.mabibli.exception.LibraryAlreadyExistsException;
import com.hugo.mabibli.exception.LibraryNotFoundException;
import com.hugo.mabibli.repository.BookRepository;
import com.hugo.mabibli.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;

    public LibraryService(
            LibraryRepository libraryRepository,
            BookRepository bookRepository
    ) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<LibraryResponse> findAll(User user) {
        return libraryRepository
                .findAllByUser_IdOrderByTitleAsc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibraryResponse findOne(User user, Long libraryId) {
        Library library = findOwnedLibrary(user, libraryId);
        return toResponse(library);
    }

    public LibraryResponse create(User user, AddLibraryRequest request) {
        String title = request.title().trim();

        if (libraryRepository.existsByTitleIgnoreCaseAndUser_Id(
                title,
                user.getId()
        )) {
            throw new LibraryAlreadyExistsException();
        }

        LocalDate now = LocalDate.now();

        Library library = new Library();
        library.setTitle(title);
        library.setUser(user);
        library.setCreatedAt(now);
        library.setUpdatedAt(now);

        return toResponse(libraryRepository.save(library));
    }

    public LibraryResponse update(
            User user,
            Long libraryId,
            UpdateLibraryRequest request
    ) {
        Library library = findOwnedLibrary(user, libraryId);
        String title = request.title().trim();

        if (!library.getTitle().equalsIgnoreCase(title)
                && libraryRepository.existsByTitleIgnoreCaseAndUser_Id(
                title,
                user.getId()
        )) {
            throw new LibraryAlreadyExistsException();
        }

        library.setTitle(title);
        library.setUpdatedAt(LocalDate.now());

        return toResponse(library);
    }

    public void delete(User user, Long libraryId) {
        Library library = findOwnedLibrary(user, libraryId);

        bookRepository.deleteAllByLibrary_IdAndLibrary_User_Id(
                libraryId,
                user.getId()
        );

        libraryRepository.delete(library);
    }

    private Library findOwnedLibrary(User user, Long libraryId) {
        return libraryRepository
                .findByIdAndUser_Id(libraryId, user.getId())
                .orElseThrow(LibraryNotFoundException::new);
    }

    private LibraryResponse toResponse(Library library) {
        return new LibraryResponse(
                library.getId(),
                library.getTitle(),
                library.getCreatedAt(),
                library.getUpdatedAt()
        );
    }
}
