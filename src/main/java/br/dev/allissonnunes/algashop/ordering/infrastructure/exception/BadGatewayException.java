package br.dev.allissonnunes.algashop.ordering.infrastructure.exception;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

}
