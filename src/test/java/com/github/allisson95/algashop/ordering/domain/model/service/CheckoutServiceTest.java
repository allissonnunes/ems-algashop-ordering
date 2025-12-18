package com.github.allisson95.algashop.ordering.domain.model.service;

import com.github.allisson95.algashop.ordering.domain.model.entity.*;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.Shipping;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CheckoutServiceTest {

    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void shouldCheckout() {
        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        final CustomerId customerId = shoppingCart.customerId();
        final Set<ShoppingCartItem> shoppingCartItems = new LinkedHashSet<>(shoppingCart.items());

        final Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        assertWith(order,
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.PLACED)
        );
        assertThatCollection(order.items())
                .extracting(OrderItem::productId, OrderItem::productName, OrderItem::price, OrderItem::quantity)
                .contains(shoppingCartItems.stream()
                        .map(item -> tuple(item.productId(), item.productName(), item.price(), item.quantity()))
                        .toArray(Tuple[]::new));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

}