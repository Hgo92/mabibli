package com.hugo.mabibli.exception;

public class LibraryNotFoundException extends RuntimeException {
    public LibraryNotFoundException() { super("Bibliothèque introuvable"); }
}
