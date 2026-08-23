package com.hugo.mabibli.dto;

public record BookSearchResult (
        String openLibraryId,
        String title,
        String author,
        String isbn,
        String coverUrl,
        Integer firstPublishYear){
}
