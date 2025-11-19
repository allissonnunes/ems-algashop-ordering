package com.github.allisson95.algashop.ordering.domain.exception;

import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderId;

public class OrderCannotBePlacedException extends DomainException {

    public OrderCannotBePlacedException(final OrderId orderId, final String cause) {
        super("Order %s cannot be placed: %s".formatted(orderId, cause));
    }

    public static OrderCannotBePlacedException becauseHasNoBillingInfo(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no billing info");
    }

    public static OrderCannotBePlacedException becauseHasNoShippingInfo(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no shipping info");
    }

    public static OrderCannotBePlacedException becauseHasNoShippingCost(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no shipping cost");
    }

    public static OrderCannotBePlacedException becauseHasNoExpectedDeliveryDate(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no expected delivery date");
    }

    public static OrderCannotBePlacedException becauseHasNoPaymentMethod(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no payment method");
    }

    public static OrderCannotBePlacedException becauseHasNoOrderItems(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no items to be placed");
    }

}
