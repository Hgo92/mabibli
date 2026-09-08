package com.hugo.mabibli.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials() {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Identifiants invalides"
        );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameExists(
            UsernameAlreadyExistsException exception
    ) {
        return problem(
                HttpStatus.CONFLICT,
                "USERNAME_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "La requête contient des valeurs invalides"
        );

        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getConstraintViolations().forEach(violation ->
                errors.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                )
        );

        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST,
                "CONSTRAINT_VIOLATION",
                "Un paramètre de la requête est invalide"
        );

        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage() {
        return problem(
                HttpStatus.BAD_REQUEST,
                "INVALID_JSON",
                "Le corps JSON est absent ou invalide"
        );
    }

    @ExceptionHandler({
            LibraryNotFoundException.class,
            BookNotFoundException.class,
            SeriesNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return problem(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            LibraryAlreadyExistsException.class,
            BookAlreadyExistsException.class,
            SeriesAlreadyExistsException.class
    })
    public ProblemDetail handleConflict(RuntimeException exception) {
        return problem(
                HttpStatus.CONFLICT,
                "RESOURCE_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation() {
        return problem(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "Cette opération entre en conflit avec une donnée existante"
        );
    }

    @ExceptionHandler(OpenLibraryUnavailableException.class)
    public ProblemDetail handleOpenLibraryUnavailable(
            OpenLibraryUnavailableException exception
    ) {
        return problem(
                HttpStatus.SERVICE_UNAVAILABLE,
                "OPEN_LIBRARY_UNAVAILABLE",
                exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Erreur inattendue sur {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Une erreur interne est survenue"
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String code,
            String message
    ) {
        ProblemDetail detail =
                ProblemDetail.forStatusAndDetail(status, message);

        detail.setTitle(status.getReasonPhrase());
        detail.setType(URI.create(
                "https://mabibli.example/errors/" + code.toLowerCase()
        ));
        detail.setProperty("code", code);

        return detail;
    }
}