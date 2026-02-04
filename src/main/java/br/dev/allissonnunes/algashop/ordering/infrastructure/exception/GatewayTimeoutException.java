package br.dev.allissonnunes.algashop.ordering.infrastructure.exception;

public class GatewayTimeoutException extends RuntimeException {

    public GatewayTimeoutException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

}
