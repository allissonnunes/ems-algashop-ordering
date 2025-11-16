package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record ProductName(String value) {

    public ProductName {
        Objects.requireNonNull(value, "productName cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("productName cannot be blank");
        }
    }

    @Override
    public @NonNull String toString() {
        return value;
    }

}
