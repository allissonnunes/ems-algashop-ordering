package com.github.allisson95.algashop.ordering.domain.exception;

import com.github.allisson95.algashop.ordering.domain.entity.OrderStatus;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(final OrderId id, final OrderStatus status) {
        super("Order %s with status %s cannot be edited".formatted(id, status));
    }

}
