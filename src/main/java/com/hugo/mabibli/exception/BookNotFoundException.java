package com.hugo.mabibli.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() { super("Livre introuvable"); }
}