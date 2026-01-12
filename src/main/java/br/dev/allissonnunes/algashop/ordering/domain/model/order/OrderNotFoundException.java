package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class OrderNotFoundException extends DomainEntityNotFoundException {

    public OrderNotFoundException(final OrderId orderId) {
        super("Order %s not found".formatted(orderId));
    }

}
