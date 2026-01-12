package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.commons.Email;
import br.dev.allissonnunes.algashop.ordering.domain.model.commons.FullName;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public record CustomerRegisteredEvent(
        CustomerId customerId,
        Instant registeredOn,
        FullName fullName,
        Email email
) {

    public CustomerRegisteredEvent {
        requireNonNull(customerId, "customerId cannot be null");
        requireNonNull(registeredOn, "registeredOn cannot be null");
        requireNonNull(fullName, "fullName cannot be null");
        requireNonNull(email, "email cannot be null");
    }

    public static CustomerRegisteredEvent of(final Customer customer) {
        return new CustomerRegisteredEvent(customer.getId(), customer.getRegisteredAt(), customer.getFullName(), customer.getEmail());
    }

}
