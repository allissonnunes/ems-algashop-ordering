package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record FullName(String firstName, String lastName) {

    public FullName {
        requireNonNull(firstName, "firstName cannot be null");
        requireNonNull(lastName, "lastName cannot be null");

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
