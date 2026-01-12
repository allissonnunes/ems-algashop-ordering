package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(final OrderId id, final OrderStatus status, final OrderStatus newStatus) {
        super("Cannot change order %s status from %s to %s".formatted(id, status, newStatus));
    }

}
