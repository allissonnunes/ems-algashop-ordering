package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import org.jspecify.annotations.NonNull;

import java.time.Clock;
import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public record BirthDate(LocalDate value) {

    public BirthDate {
        requireNonNull(value, "birthDate cannot be null");
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate cannot be in the future");
        }
    }

    public Integer age(final Clock ageCalculationClock) {
        return value.until(LocalDate.now(ageCalculationClock)).getYears();
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
