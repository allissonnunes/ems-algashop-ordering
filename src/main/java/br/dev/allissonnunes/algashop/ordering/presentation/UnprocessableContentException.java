package br.dev.allissonnunes.algashop.ordering.presentation;

public class UnprocessableContentException extends RuntimeException {

    public UnprocessableContentException(final String message, final Throwable cause) {
        super(message, cause, true, false);
    }

}
