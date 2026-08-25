package com.hugo.mabibli.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_book_library_open_library",
                columnNames = {
                        "library_id",
                        "open_library_id"
                }
        )
)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 25)
    private String isbn;

    @Column(name = "open_library_id", nullable = false, length = 50)
    private String openLibraryId;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    private Integer seriesIndex;

    @Column(nullable = false, length = 255)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "reading_date")
    private LocalDate readingDate;

    @Column(columnDefinition = "TEXT", length=1000)
    private String description;

    @Column(length = 500)
    private String cover;

    private Integer pages;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(
            name = "category",
            nullable = false,
            length = 50
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    public Book() {}

    // Mes setters et getters (que je pourrais simplifier avec Lombok mais je préfère les avoir en dur pour apprendre)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getOpenLibraryId() { return openLibraryId; }
    public void setOpenLibraryId(String openLibraryId) { this.openLibraryId = openLibraryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Series getSeries() { return series; }
    public void setSeries(Series series) { this.series = series; }

    public Integer getSeriesIndex() { return seriesIndex; }
    public void setSeriesIndex(Integer seriesIndex) { this.seriesIndex = seriesIndex; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

}
