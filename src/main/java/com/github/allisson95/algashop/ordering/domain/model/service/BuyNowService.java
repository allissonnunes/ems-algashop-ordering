package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.model.utility.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Shipping;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static java.util.Objects.requireNonNull;

@DomainService
public class BuyNowService {

    public Order buyNow(final Product product,
                        final CustomerId customerId,
                        final Billing billing,
                        final Shipping shipping,
                        final Quantity quantity,
                        final PaymentMethod paymentMethod) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(customerId, "customerId cannot be null");
        requireNonNull(billing, "billing cannot be null");
        requireNonNull(shipping, "shipping cannot be null");
        requireNonNull(quantity, "quantity cannot be null");
        requireNonNull(paymentMethod, "paymentMethod cannot be null");

        product.checkOutOfStock();

        if (Quantity.ZERO.equals(quantity)) {
            throw new IllegalArgumentException("quantity cannot be zero");
        }

        final Order newOrder = Order.draft(customerId);
        newOrder.changeBilling(billing);
        newOrder.changeShipping(shipping);
        newOrder.changePaymentMethod(paymentMethod);
        newOrder.addItem(product, quantity);

        newOrder.place();

        return newOrder;
    }

}
