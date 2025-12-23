package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.customer.CustomerId;
import com.github.allisson95.algashop.ordering.domain.model.product.Product;
import com.github.allisson95.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.github.allisson95.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CheckoutServiceTest {

    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void shouldCheckout() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final CustomerId customerId = shoppingCart.customerId();
        final Set<ShoppingCartItem> shoppingCartItems = new LinkedHashSet<>(shoppingCart.items());

        final Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        assertWith(order,
                o -> assertThat(o.getBilling()).isEqualTo(billing),
                o -> assertThat(o.getShipping()).isEqualTo(shipping),
                o -> assertThat(o.getPaymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.getCustomerId()).isEqualTo(customerId),
                o -> assertThat(o.getStatus()).isEqualTo(OrderStatus.PLACED)
        );
        assertThatCollection(order.getItems())
                .extracting(OrderItem::productId, OrderItem::productName, OrderItem::price, OrderItem::quantity, OrderItem::totalAmount)
                .contains(shoppingCartItems.stream()
                        .map(item -> tuple(item.productId(), item.productName(), item.price(), item.quantity(), item.totalAmount()))
                        .toArray(Tuple[]::new));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenCheckoutShoppingCartWithUnavailableProducts() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        final ShoppingCartItem shoppingCartItem = shoppingCart.items().iterator().next();
        final Product unavailableProduct = ProductTestDataBuilder.anOutOfStockProduct()
                .id(shoppingCartItem.productId())
                .name(shoppingCartItem.productName())
                .price(shoppingCartItem.price())
                .build();
        shoppingCart.refreshItem(unavailableProduct);

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod));
        assertThat(shoppingCart.isEmpty()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenCheckoutShoppingCartIsEmpty() {
        final Billing billing = OrderTestDataBuilder.aBilling();
        final Shipping shipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        final ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCart.empty();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod));
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

}