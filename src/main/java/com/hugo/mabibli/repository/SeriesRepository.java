package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findAllByUser_IdOrderByTitleAsc(Long userId);

    Optional<Series> findByIdAndUser_Id(Long seriesId, Long userId);

    boolean existsByTitleIgnoreCaseAndUser_Id(String title, Long userId);
}