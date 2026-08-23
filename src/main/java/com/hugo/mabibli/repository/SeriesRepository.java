package com.hugo.mabibli.repository;

import com.hugo.mabibli.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByUserId(Long userId);
}