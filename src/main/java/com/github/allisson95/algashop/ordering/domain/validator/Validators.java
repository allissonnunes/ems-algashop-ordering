package com.github.allisson95.algashop.ordering.domain.validator;

import org.apache.commons.validator.routines.EmailValidator;

public final class Validators {

    private Validators() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidEmail(final String email) {
        return EmailValidator.getInstance().isValid(email);
    }

}
