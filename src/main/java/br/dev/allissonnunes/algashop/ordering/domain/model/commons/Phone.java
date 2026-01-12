package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Phone(String value) {

    public Phone {
        requireNonNull(value, "phone cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("phone cannot be blank");
        }
    }

    @Override
    public @NonNull String toString() {
        return value;
    }

}
