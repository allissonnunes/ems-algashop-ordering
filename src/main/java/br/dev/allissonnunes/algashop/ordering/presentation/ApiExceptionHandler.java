package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainEntityNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(CustomerEmailIsInUseException.class)
    public ResponseEntity<Object> handleCustomerEmailIsInUseException(final CustomerEmailIsInUseException ex, final @NonNull WebRequest request) {
        final HttpStatus conflict = HttpStatus.CONFLICT;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(conflict);
        problemDetail.setTitle(conflict.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/conflict"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), conflict, request);
    }

    @ExceptionHandler(DomainEntityNotFoundException.class)
    public ResponseEntity<Object> handleDomainEntityNotFoundException(final DomainEntityNotFoundException ex, final @NonNull WebRequest request) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(notFound);
        problemDetail.setTitle(notFound.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/not-found"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), notFound, request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(final DomainException ex, final @NonNull WebRequest request) {
        final HttpStatus unprocessableContent = HttpStatus.UNPROCESSABLE_CONTENT;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(unprocessableContent);
        problemDetail.setTitle(unprocessableContent.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/unprocessable-content"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), unprocessableContent, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(final Exception ex, final @NonNull WebRequest request) {
        logger.error("An unexpected error occurred", ex);

        final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(internalServerError);
        problemDetail.setTitle(internalServerError.getReasonPhrase());
        problemDetail.setDetail("An unexpected error occurred. Please try again later.");
        problemDetail.setType(URI.create("/errors/internal-server-error"));

        return super.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), internalServerError, request);
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
