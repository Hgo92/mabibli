package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>{
    List<Book> findAllByLibrary_IdAndLibrary_User_IdOrderByTitleAsc(
            Long libraryId,
            Long userId
    );

    Optional<Book> findByIdAndLibrary_User_Id(
            Long bookId,
            Long userId
    );

    Optional<Book> findByIdAndLibrary_IdAndLibrary_User_Id(
            Long bookId,
            Long libraryId,
            Long userId
    );

    void deleteAllByLibrary_IdAndLibrary_User_Id(
            Long libraryId,
            Long userId
    );

    boolean existsByOpenLibraryIdAndLibrary_Id(
            String openLibraryId,
            Long libraryId
    );

    boolean existsByLibrary_Id(Long libraryId);}
