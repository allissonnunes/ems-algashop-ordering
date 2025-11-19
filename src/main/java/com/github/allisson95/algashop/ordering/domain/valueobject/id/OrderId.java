package com.github.allisson95.algashop.ordering.domain.valueobject.id;

import com.github.allisson95.algashop.ordering.domain.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record OrderId(UUID value) {

    public OrderId {
        requireNonNull(value, "id cannot be null");
    }

    public OrderId() {
        this(IdGenerator.generate());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
