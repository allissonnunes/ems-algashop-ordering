package com.github.allisson95.algashop.ordering.domain.factory;

import com.github.allisson95.algashop.ordering.domain.entity.Order;
import com.github.allisson95.algashop.ordering.domain.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.Shipping;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;

import java.util.Objects;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(final CustomerId customerId, final Billing billing, final Shipping shipping, final PaymentMethod paymentMethod, final Product product, final Quantity productQuantity) {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(shipping, "shipping cannot be null");
        Objects.requireNonNull(billing, "billing cannot be null");
        Objects.requireNonNull(paymentMethod, "paymentMethod cannot be null");
        Objects.requireNonNull(product, "product cannot be null");
        Objects.requireNonNull(productQuantity, "productQuantity cannot be null");

        final Order order = Order.draft(customerId);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, productQuantity);

        return order;
    }

}
