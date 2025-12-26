package com.github.allisson95.algashop.ordering.domain.model.customer;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public record CustomerArchivedEvent(CustomerId customerId, Instant archivedOn) {

    public CustomerArchivedEvent {
        requireNonNull(customerId, "customerId cannot be null");
        requireNonNull(archivedOn, "archivedOn cannot be null");
    }

}
