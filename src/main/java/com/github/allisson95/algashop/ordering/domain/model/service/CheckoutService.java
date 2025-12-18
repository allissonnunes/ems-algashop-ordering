package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.Order;
import com.github.allisson95.algashop.ordering.domain.model.entity.PaymentMethod;
import com.github.allisson95.algashop.ordering.domain.model.entity.ShoppingCart;
import com.github.allisson95.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.github.allisson95.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.github.allisson95.algashop.ordering.domain.model.utility.DomainService;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Shipping;

import static java.util.Objects.requireNonNull;

@DomainService
public class CheckoutService {

    public Order checkout(final ShoppingCart shoppingCart, final Billing billing, final Shipping shipping, final PaymentMethod paymentMethod) {
        requireNonNull(shoppingCart, "Shopping cart cannot be null");
        requireNonNull(billing, "Billing information cannot be null");
        requireNonNull(shipping, "Shipping information cannot be null");
        requireNonNull(paymentMethod, "Payment method cannot be null");

        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }

        final Order newOrder = Order.draft(shoppingCart.customerId());
        newOrder.changeBilling(billing);
        newOrder.changeShipping(shipping);
        newOrder.changePaymentMethod(paymentMethod);

        for (final ShoppingCartItem sci : shoppingCart.items()) {
            final Product product = getProduct(sci);
            newOrder.addItem(product, sci.quantity());
        }

        newOrder.place();

        shoppingCart.empty();

        return newOrder;
    }

    private Product getProduct(final ShoppingCartItem sci) {
        return Product.builder()
                .id(sci.productId())
                .name(sci.productName())
                .price(sci.price())
                .inStock(sci.isAvailable())
                .build();
    }

}
