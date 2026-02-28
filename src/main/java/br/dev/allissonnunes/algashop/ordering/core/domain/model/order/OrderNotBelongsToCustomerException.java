package br.dev.allissonnunes.algashop.ordering.core.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.core.domain.model.DomainException;

public class OrderNotBelongsToCustomerException extends DomainException {

    public OrderNotBelongsToCustomerException() {
        super("Order not belongs to customer");
    }

}
