package br.dev.allissonnunes.algashop.ordering.domain.model.customer;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class CustomerNotFoundException extends DomainEntityNotFoundException {

    public CustomerNotFoundException(final CustomerId customerId) {
        super("Customer %s not found".formatted(customerId));
    }

}
