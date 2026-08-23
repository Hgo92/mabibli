package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.BookSearchResult;
import com.hugo.mabibli.dto.openlibrary.OpenLibraryDoc;
import com.hugo.mabibli.dto.openlibrary.OpenLibrarySearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OpenLibraryService {
    private final RestClient openLibraryRestClient;

    public OpenLibraryService(RestClient openLibraryRestClient) {
        this.openLibraryRestClient = openLibraryRestClient;
    }

    public List<BookSearchResult> search(String query) {
        OpenLibrarySearchResponse response = openLibraryRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q", query)
                        .queryParam("fields", "key,title,author_name,cover_i,first_publish_year,isbn")
                        .queryParam("limit", 20)
                        .build())
                .retrieve()
                .body(OpenLibrarySearchResponse.class);

        if (response == null || response.docs() == null) {
            return List.of();
        }

        return response.docs().stream().map(this::toSearchResult).toList();
    }

    private BookSearchResult toSearchResult(OpenLibraryDoc doc) {
        String coverUrl = doc.coverId() != null
                ? "https://covers.openlibrary.org/b/id/" + doc.coverId() + "-M.jpg"
                : null;
        String author = (doc.authorName() != null && !doc.authorName().isEmpty())
                ? doc.authorName().get(0) : "Auteur inconnu";
        String isbn = (doc.isbn() != null && !doc.isbn().isEmpty())
                ? doc.isbn().get(0) : null;

        return new BookSearchResult(extractId(doc.key()), doc.title(), author, isbn, coverUrl, doc.firstPublishYear());
    }

    private String extractId(String key) {
        if (key == null) return null;
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key; // "/works/OL45804W" -> "OL45804W"
    }

}
