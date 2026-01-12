package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Document(String value) {

    public Document {
        requireNonNull(value, "document cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("document cannot be blank");
        }
    }

    @Override
    public @NonNull String toString() {
        return value;
    }

}
