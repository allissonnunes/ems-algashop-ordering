package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.DomainException;

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

    public static OrderCannotBePlacedException becauseHasNoPaymentMethod(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no payment method");
    }

    public static OrderCannotBePlacedException becauseHasNoOrderItems(final OrderId orderId) {
        return new OrderCannotBePlacedException(orderId, "Has no items to be placed");
    }

}
