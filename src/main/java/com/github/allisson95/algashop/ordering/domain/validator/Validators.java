package com.github.allisson95.algashop.ordering.domain.validator;

import org.apache.commons.validator.routines.EmailValidator;

import static java.util.Objects.requireNonNull;

public final class Validators {

    private Validators() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidEmail(final String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static String requireNonBlank(final String value, final String message) {
        if (requireNonNull(value, message).isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

}
