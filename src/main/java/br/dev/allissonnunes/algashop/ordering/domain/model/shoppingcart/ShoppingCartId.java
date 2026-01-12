package br.dev.allissonnunes.algashop.ordering.domain.model.shoppingcart;

import br.dev.allissonnunes.algashop.ordering.domain.model.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        requireNonNull(value, "id cannot be null");
    }

    public ShoppingCartId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    @Override
    public @NonNull String toString() {
        return value.toString();
    }

}
