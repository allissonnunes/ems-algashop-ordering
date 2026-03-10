package br.dev.allissonnunes.algashop.ordering.infrastructure.config.errorhandling;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

}
