package com.github.allisson95.algashop.ordering.domain.model;

public class DomainEntityNotFoundException extends RuntimeException {

    public DomainEntityNotFoundException(String message) {
        this(message, null);
    }

    public DomainEntityNotFoundException(String message, Throwable cause) {
        super(message, cause, true, false);
    }

}
