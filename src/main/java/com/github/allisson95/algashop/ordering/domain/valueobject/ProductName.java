package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record ProductName(String value) {

    public ProductName {
        requireNonNull(value, "productName cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("productName cannot be blank");
        }
    }

    @Override
    public @NonNull String toString() {
        return value;
    }

}
