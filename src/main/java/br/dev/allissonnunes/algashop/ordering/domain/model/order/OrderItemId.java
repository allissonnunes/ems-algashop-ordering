package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.IdGenerator;
import io.hypersistence.tsid.TSID;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record OrderItemId(TSID value) {

    public OrderItemId {
        requireNonNull(value, "id cannot be null");
    }

    public OrderItemId(final Long value) {
        this(TSID.from(value));
    }

    public OrderItemId() {
        this(IdGenerator.gererateTSID());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
