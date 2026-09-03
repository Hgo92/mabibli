package com.hugo.mabibli.exception;

public class SeriesNotFoundException extends RuntimeException {
    public SeriesNotFoundException() {
        super("Série introuvable");
    }
}
