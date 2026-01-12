package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record ZipCode(String value) {

    public ZipCode {
        requireNonNull(value, "zipCode cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("zipCode cannot be blank");
        }
        if (value.length() != 5) {
            throw new IllegalArgumentException("zipCode must have 5 digits");
        }
    }

    @Override
    public @NonNull String toString() {
        return value;
    }

}
