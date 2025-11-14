package com.github.allisson95.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record LoyaltyPoints(Integer value) implements Comparable<LoyaltyPoints> {

    public static final LoyaltyPoints ZERO = new LoyaltyPoints(0);

    public LoyaltyPoints {
        if (value < 0) {
            throw new IllegalArgumentException("loyaltyPoints cannot be negative");
        }
    }

    public LoyaltyPoints add(final LoyaltyPoints loyaltyPointsToAdd) {
        Objects.requireNonNull(loyaltyPointsToAdd, "loyaltyPointsToAdd cannot be null");
        if (loyaltyPointsToAdd.value() <= 0) {
            throw new IllegalArgumentException("loyaltyPointsToAdd cannot be negative or zero");
        }
        return new LoyaltyPoints(this.value() + loyaltyPointsToAdd.value());
    }

    @Override
    public int compareTo(final LoyaltyPoints o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
