package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(final OrderId id, final OrderStatus status) {
        super("Order %s with status %s cannot be edited".formatted(id, status));
    }

}
