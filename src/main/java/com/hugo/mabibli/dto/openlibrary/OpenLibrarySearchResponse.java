package com.hugo.mabibli.dto.openlibrary;

import java.util.List;

public record OpenLibrarySearchResponse(int numFound, List<OpenLibraryDoc> docs) {
}
