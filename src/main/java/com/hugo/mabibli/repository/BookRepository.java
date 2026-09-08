package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

public interface BookRepository extends JpaRepository<Book, Long>{
    @EntityGraph(attributePaths = "series")
    Page<Book> findAllByLibrary_IdAndLibrary_User_Id(
            Long libraryId,
            Long userId,
            Pageable pageable
    );

    List<Book> findAllBySeries_IdAndLibrary_User_Id(
            Long seriesId,
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
