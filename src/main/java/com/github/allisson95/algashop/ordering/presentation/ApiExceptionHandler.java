package com.github.allisson95.algashop.ordering.presentation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    public ApiExceptionHandler(final MessageSource messageSource) {
        super.setMessageSource(messageSource);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                            final @NonNull HttpHeaders headers,
                                                                            final @NonNull HttpStatusCode status,
                                                                            final @NonNull WebRequest request) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Invalid fields");
        problemDetail.setDetail("One or more fields are invalid.");
        problemDetail.setType(URI.create("/errors/invalid-fields"));

        final Map<String, String> fieldErrors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> {
                            var invalidField = error.getObjectName();
                            if (error instanceof FieldError fieldError) {
                                invalidField = fieldError.getField();
                            }
                            return invalidField;
                        },
                        error -> requireNonNull(getMessageSource()).getMessage(error, request.getLocale())));
        problemDetail.setProperty("fields", fieldErrors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

}
