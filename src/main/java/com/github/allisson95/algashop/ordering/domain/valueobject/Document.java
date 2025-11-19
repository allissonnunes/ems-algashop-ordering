package com.github.allisson95.algashop.ordering.domain.valueobject;

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
