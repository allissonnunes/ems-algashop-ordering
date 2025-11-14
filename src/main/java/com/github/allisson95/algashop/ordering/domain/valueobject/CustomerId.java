package com.github.allisson95.algashop.ordering.domain.valueobject;

import com.github.allisson95.algashop.ordering.domain.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId() {
        this(IdGenerator.generate());
    }

    public CustomerId {
        Objects.requireNonNull(value, "id cannot be null");
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
