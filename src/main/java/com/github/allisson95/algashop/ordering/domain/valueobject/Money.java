package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money> {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(value, "money cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("money cannot be negative");
        }
        value = value.setScale(2, ROUNDING_MODE);
    }

    public Money(final String value) {
        Objects.requireNonNull(value, "money cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("money cannot be blank");
        }
        this(new BigDecimal(value));
    }

    public Money add(final Money moneyToAdd) {
        Objects.requireNonNull(moneyToAdd, "moneyToAdd cannot be null");
        return new Money(this.value().add(moneyToAdd.value()));
    }

    public Money multiply(final Quantity quantity) {
        Objects.requireNonNull(quantity, "quantity cannot be null");
        if (quantity.compareTo(Quantity.ZERO) <= 0) {
            throw new IllegalArgumentException("quantity cannot be negative or zero");
        }
        return new Money(this.value().multiply(BigDecimal.valueOf(quantity.value())));
    }

    @Override
    public int compareTo(final Money o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public @NonNull String toString() {
        return DecimalFormat.getNumberInstance().format(value);
    }

}
