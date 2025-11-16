package com.github.allisson95.algashop.ordering.domain.valueobject.id;

import com.github.allisson95.algashop.ordering.domain.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "id cannot be null");
    }

    public CustomerId() {
        this(IdGenerator.generate());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
