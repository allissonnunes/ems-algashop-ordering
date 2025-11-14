package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record ZipCode(String value) {

    public ZipCode {
        Objects.requireNonNull(value, "zipCode cannot be null");
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
