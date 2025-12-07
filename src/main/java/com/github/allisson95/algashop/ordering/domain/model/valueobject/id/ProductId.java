package com.github.allisson95.algashop.ordering.domain.model.valueobject.id;

import com.github.allisson95.algashop.ordering.domain.model.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ProductId(UUID value) {

    public ProductId {
        requireNonNull(value, "id cannot be null");
    }

    public ProductId(final String value) {
        this(UUID.fromString(value));
    }

    public ProductId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
