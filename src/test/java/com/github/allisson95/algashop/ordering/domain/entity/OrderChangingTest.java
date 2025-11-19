package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.github.allisson95.algashop.ordering.domain.valueobject.Billing;
import com.github.allisson95.algashop.ordering.domain.valueobject.Product;
import com.github.allisson95.algashop.ordering.domain.valueobject.Quantity;
import com.github.allisson95.algashop.ordering.domain.valueobject.Shipping;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.OrderItemId;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderChangingTest {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void givenOrderWithStatusDifferentFromPlaced_whenUpdateStatus_shouldThrowException(final OrderStatus status) {
        final Order order = OrderTestDataBuilder.anOrder().status(status).build();
        final Billing newBilling = OrderTestDataBuilder.aBilling();
        final Shipping newShipping = OrderTestDataBuilder.aShipping();
        final PaymentMethod newPaymentMethod = PaymentMethod.CREDIT_CARD;
        final Product product = ProductTestDataBuilder.aProduct().build();
        final Quantity quantity = new Quantity(1);
        final OrderItem orderItem = order.items().iterator().next();
        final OrderItemId orderItemId = orderItem.id();
        final Quantity newQuantity = new Quantity(2);

        final List<ThrowableAssert.ThrowingCallable> tasks = List.of(
                () -> order.changeBilling(newBilling),
                () -> order.changeShipping(newShipping),
                () -> order.changePaymentMethod(newPaymentMethod),
                () -> order.addItem(product, quantity),
                () -> order.changeItemQuantity(orderItemId, newQuantity)
        );

        tasks.forEach(task -> {
            if (OrderStatus.DRAFT.equals(status)) {
                assertThatCode(task).doesNotThrowAnyException();
            } else {
                assertThatThrownBy(task).isInstanceOf(OrderCannotBeEditedException.class);
            }
        });
    }

}
