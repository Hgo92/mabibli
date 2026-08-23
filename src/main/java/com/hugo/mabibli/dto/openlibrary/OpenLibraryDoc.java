package com.hugo.mabibli.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryDoc(
        String key,
        String title,
        @JsonProperty("author_name") List<String> authorName,
        @JsonProperty("cover_i") Integer coverId,
        @JsonProperty("first_publish_year") Integer firstPublishYear,
        List<String> isbn
        ) {
}
