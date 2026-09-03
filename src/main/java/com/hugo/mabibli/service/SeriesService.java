package com.hugo.mabibli.service;

// Import de mes DTO
import com.hugo.mabibli.dto.AddSeriesRequest;
import com.hugo.mabibli.dto.SeriesResponse;
import com.hugo.mabibli.dto.UpdateSeriesRequest;

// Import de mes entities
import com.hugo.mabibli.entity.Book;
import com.hugo.mabibli.entity.Series;
import com.hugo.mabibli.entity.User;

// Import de mes repositories
import com.hugo.mabibli.repository.BookRepository;
import com.hugo.mabibli.repository.SeriesRepository;

// Import de mes exceptions
import com.hugo.mabibli.exception.SeriesAlreadyExistsException;
import com.hugo.mabibli.exception.SeriesNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final BookRepository bookRepository;

    public SeriesService(
            SeriesRepository seriesRepository,
            BookRepository bookRepository
    ) {
        this.seriesRepository = seriesRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<SeriesResponse> findAll (User user) {
        return seriesRepository.findAllByUser_IdOrderByTitleAsc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeriesResponse findOne(User user, Long seriesId) {
        return toResponse(findOwnedSeries(user,seriesId));
    }

    public SeriesResponse create(User user, AddSeriesRequest request) {
        String title = request.title().trim();

        if (seriesRepository.existsByTitleIgnoreCaseAndUser_Id(title, user.getId())) {
            throw new SeriesAlreadyExistsException();
        }

        LocalDate now = LocalDate.now();
        Series series = new Series();

        series.setTitle(title);
        series.setUser(user);
        series.setCreatedAt(now);
        series.setUpdatedAt(now);

        return toResponse(seriesRepository.save(series));
    }

    public SeriesResponse update(User user, Long seriesId, UpdateSeriesRequest request) {
        Series series = findOwnedSeries(user, seriesId);
        String title = request.title().trim();

        if (!series.getTitle().equalsIgnoreCase(title)
                && seriesRepository.existsByTitleIgnoreCaseAndUser_Id(title, user.getId())
        ) {
            throw new SeriesAlreadyExistsException();
        }

        series.setTitle(title);
        series.setUpdatedAt(LocalDate.now());
        return toResponse(series);
    }

    public void delete(User user, Long seriesId) {
        Series series = findOwnedSeries(user, seriesId);
        List<Book> books = bookRepository.findAllBySeries_IdAndLibrary_User_Id(seriesId, user.getId());

        for (Book book : books) {
            book.setSeries(null);
            book.setSeriesIndex(null);
            book.setUpdatedAt(LocalDate.now());
        }
        seriesRepository.delete(series);
    }

    private Series findOwnedSeries(User user, Long seriesId) {
        return seriesRepository
                .findByIdAndUser_Id(seriesId, user.getId())
                .orElseThrow(SeriesNotFoundException::new);
    }

    private SeriesResponse toResponse(Series series) {
        return new SeriesResponse(
                series.getId(),
                series.getTitle(),
                series.getCreatedAt(),
                series.getUpdatedAt()
        );
    }
}
