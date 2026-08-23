package com.hugo.mabibli.exception;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException() { super("Ce livre est déjà dans ta bibliothèque"); }
}