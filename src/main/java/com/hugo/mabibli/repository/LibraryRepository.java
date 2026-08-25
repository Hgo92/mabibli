package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, Long> {

    List<Library> findAllByUser_IdOrderByTitleAsc(Long userId);

    Optional<Library> findByIdAndUser_Id(Long libraryId, Long userId);

    boolean existsByTitleIgnoreCaseAndUser_Id(String title, Long userId);
}
