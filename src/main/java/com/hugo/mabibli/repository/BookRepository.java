package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>{

    List<Book> findByUserId(Long userId);
    List<Book> findByLibraryId(Long libraryId);

    Optional<Book> findByOpenLibraryId(String openLibraryId);
    boolean existsByOpenLibraryId(String openLibraryId);
    boolean existsByOpenLibraryIdAndUserId(String openLibraryId, Long userId);
}
