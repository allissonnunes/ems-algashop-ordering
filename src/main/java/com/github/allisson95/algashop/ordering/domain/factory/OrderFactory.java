package com.github.allisson95.algashop.ordering.domain.factory;

import com.github.allisson95.algashop.ordering.domain.entity.Order;
import com.github.allisson95.algashop.ordering.domain.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.Shipping;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;

import static java.util.Objects.requireNonNull;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(final CustomerId customerId, final Billing billing, final Shipping shipping, final PaymentMethod paymentMethod, final Product product, final Quantity productQuantity) {
        requireNonNull(customerId, "customerId cannot be null");
        requireNonNull(shipping, "shipping cannot be null");
        requireNonNull(billing, "billing cannot be null");
        requireNonNull(paymentMethod, "paymentMethod cannot be null");
        requireNonNull(product, "product cannot be null");
        requireNonNull(productQuantity, "productQuantity cannot be null");

        final Order order = Order.draft(customerId);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, productQuantity);

        return order;
    }

}
