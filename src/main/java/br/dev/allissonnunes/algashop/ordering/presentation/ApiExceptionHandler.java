package br.dev.allissonnunes.algashop.ordering.presentation;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainEntityNotFoundException;
import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;
import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.exception.BadGatewayException;
import br.dev.allissonnunes.algashop.ordering.infrastructure.exception.GatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@RestControllerAdvice
@Slf4j
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

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), conflict, request);
    }

    @ExceptionHandler(DomainEntityNotFoundException.class)
    public ResponseEntity<Object> handleDomainEntityNotFoundException(final DomainEntityNotFoundException ex, final @NonNull WebRequest request) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(notFound);
        problemDetail.setTitle(notFound.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/not-found"));

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), notFound, request);
    }

    @ExceptionHandler({ DomainException.class, UnprocessableContentException.class })
    public ResponseEntity<Object> handleDomainException(final Exception ex, final @NonNull WebRequest request) {
        final HttpStatus unprocessableContent = HttpStatus.UNPROCESSABLE_CONTENT;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(unprocessableContent);
        problemDetail.setTitle(unprocessableContent.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/unprocessable-content"));

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), unprocessableContent, request);
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<Object> handleBadGatewayException(final BadGatewayException ex, final @NonNull WebRequest request) {
        final HttpStatus badGateway = HttpStatus.BAD_GATEWAY;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(badGateway);
        problemDetail.setTitle(badGateway.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/bad-gateway"));

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), badGateway, request);
    }

    @ExceptionHandler(GatewayTimeoutException.class)
    public ResponseEntity<Object> handleGatewayTimeoutException(final GatewayTimeoutException ex, final @NonNull WebRequest request) {
        final HttpStatus gatewayTimeout = HttpStatus.GATEWAY_TIMEOUT;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(gatewayTimeout);
        problemDetail.setTitle(gatewayTimeout.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setType(URI.create("/errors/gateway-timeout"));

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), gatewayTimeout, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(final Exception ex, final @NonNull WebRequest request) {
        final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        final ProblemDetail problemDetail = ProblemDetail.forStatus(internalServerError);
        problemDetail.setTitle(internalServerError.getReasonPhrase());
        problemDetail.setDetail("An unexpected error occurred. Please try again later.");
        problemDetail.setType(URI.create("/errors/internal-server-error"));

        return this.handleExceptionInternal(ex, problemDetail, new HttpHeaders(), internalServerError, request);
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

        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(final @NonNull Exception ex,
                                                                       final @Nullable Object body,
                                                                       final @NonNull HttpHeaders headers,
                                                                       final @NonNull HttpStatusCode statusCode,
                                                                       final @NonNull WebRequest request) {
        logRequestError(ex, request);
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private void logRequestError(final @NonNull Exception ex, final @NonNull WebRequest request) {
        final var hsr = ((ServletWebRequest) request).getRequest();
        final var requestHttpMethod = hsr.getMethod().toUpperCase();
        final var requestURI = new StringBuffer(hsr.getRequestURI());
        if (hsr.getQueryString() != null) {
            requestURI.append(hsr.getQueryString());
        }

        log.error("[type:request] [status:error] [method:{}] [uri:{}]", requestHttpMethod, requestURI, ex);
    }

}
