package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.DomainException;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(final OrderId orderId) {
        super("Order %s not found".formatted(orderId));
    }

}
