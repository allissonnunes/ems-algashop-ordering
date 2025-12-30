package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import com.github.allisson95.algashop.ordering.domain.model.customer.Customer;
import com.github.allisson95.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

import java.time.Year;

import static java.util.Objects.requireNonNull;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final Orders orders;

    public Order buyNow(final Product product,
                        final Customer customer,
                        final Billing billing,
                        final Shipping shipping,
                        final Quantity quantity,
                        final PaymentMethod paymentMethod) {
        requireNonNull(product, "product cannot be null");
        requireNonNull(customer, "customer cannot be null");
        requireNonNull(billing, "billing cannot be null");
        requireNonNull(shipping, "shipping cannot be null");
        requireNonNull(quantity, "quantity cannot be null");
        requireNonNull(paymentMethod, "paymentMethod cannot be null");

        product.checkOutOfStock();

        if (Quantity.ZERO.equals(quantity)) {
            throw new IllegalArgumentException("quantity cannot be zero");
        }

        final Order newOrder = Order.draft(customer.getId());
        newOrder.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            newOrder.changeShipping(shipping.toBuilder().cost(Money.ZERO).build());
        } else {
            newOrder.changeShipping(shipping);
        }

        newOrder.changePaymentMethod(paymentMethod);
        newOrder.addItem(product, quantity);

        newOrder.place();

        return newOrder;
    }

    private boolean haveFreeShipping(final Customer customer) {
        return customer.getLoyaltyPoints().compareTo(new LoyaltyPoints(100)) >= 0
                && orders.salesQuantityByCustomerInYear(customer.getId(), Year.now()) >= 2
                || customer.getLoyaltyPoints().compareTo(new LoyaltyPoints(2000)) >= 0;
    }

}
