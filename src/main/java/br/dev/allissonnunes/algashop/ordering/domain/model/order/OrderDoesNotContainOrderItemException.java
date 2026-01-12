package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(final OrderId id, final OrderItemId orderItemId) {
        super("Order %s does not contain order item %s".formatted(id, orderItemId));
    }

}
