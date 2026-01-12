package br.dev.allissonnunes.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Quantity(Integer value) implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        requireNonNull(value, "quantity cannot be null");
        if (value < 0) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }
    }

    public Quantity add(final Quantity quantityToAdd) {
        requireNonNull(quantityToAdd, "quantityToAdd cannot be null");
        return new Quantity(this.value() + quantityToAdd.value());
    }

    @Override
    public int compareTo(final Quantity o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
