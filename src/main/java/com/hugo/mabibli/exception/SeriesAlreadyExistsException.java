package com.hugo.mabibli.exception;

public class SeriesAlreadyExistsException extends RuntimeException {

        public SeriesAlreadyExistsException() {
            super("Une série portant ce nom existe déjà");
        }

}
