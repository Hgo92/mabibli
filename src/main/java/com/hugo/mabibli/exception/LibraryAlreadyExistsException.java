package com.hugo.mabibli.exception;

public class LibraryAlreadyExistsException extends RuntimeException {

    public LibraryAlreadyExistsException() {
        super("Une bibliothèque portant ce nom existe déjà");
    }
}