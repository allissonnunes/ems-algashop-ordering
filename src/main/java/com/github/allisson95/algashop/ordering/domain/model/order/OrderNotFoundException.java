package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class OrderNotFoundException extends DomainEntityNotFoundException {

    public OrderNotFoundException(final OrderId orderId) {
        super("Order %s not found".formatted(orderId));
    }

}
