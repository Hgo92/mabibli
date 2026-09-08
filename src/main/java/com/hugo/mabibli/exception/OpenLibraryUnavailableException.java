package com.hugo.mabibli.exception;

public class OpenLibraryUnavailableException  extends RuntimeException {
    public OpenLibraryUnavailableException() {
        super("Le service Open Library est indisponible");
    }
}
