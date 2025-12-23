package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.IdGenerator;
import io.hypersistence.tsid.TSID;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record OrderId(TSID value) {

    public OrderId {
        requireNonNull(value, "id cannot be null");
    }

    public OrderId(Long value) {
        this(TSID.from(value));
    }

    public OrderId(String value) {
        this(TSID.from(value));
    }

    public OrderId() {
        this(IdGenerator.gererateTSID());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
