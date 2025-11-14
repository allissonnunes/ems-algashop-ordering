package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record FullName(String firstName, String lastName) {

    public FullName {
        Objects.requireNonNull(firstName, "firstName cannot be null");
        Objects.requireNonNull(lastName, "lastName cannot be null");

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be blank");
        }

        if (lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be blank");
        }

        firstName = firstName.trim();
        lastName = lastName.trim();
    }

    @Override
    public @NonNull String toString() {
        return firstName + " " + lastName;
    }

}
