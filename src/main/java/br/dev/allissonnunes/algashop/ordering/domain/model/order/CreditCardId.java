package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.IdGenerator;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record CreditCardId(UUID value) {

    public CreditCardId {
        requireNonNull(value, "value cannot be null");
    }

    public CreditCardId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

}
