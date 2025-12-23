package com.github.allisson95.algashop.ordering.domain.model.order;

import com.github.allisson95.algashop.ordering.domain.model.commons.Money;
import com.github.allisson95.algashop.ordering.domain.model.commons.Quantity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.MathContext;

import static org.assertj.core.api.Assertions.*;

class OrderRemoveItemTest {

    @Test
    void givenADraftOrderWithItems_whenRemoveItem_shouldBeOk() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        final OrderItem orderItem = order.getItems().iterator().next();
        final Quantity expectedTotalItems = new Quantity(order.getTotalItems().value() - orderItem.quantity().value());
        final Money expectedTotalAmount = new Money(order.getTotalAmount().value().subtract(orderItem.totalAmount().value(), MathContext.DECIMAL32));

        assertWith(order,
                o -> assertThatCode(() -> o.removeItem(orderItem.id())).doesNotThrowAnyException(),
                o -> assertThat(o.getItems()).doesNotContain(orderItem),
                o -> assertThat(o.getTotalItems()).isEqualTo(expectedTotalItems),
                o -> assertThat(o.getTotalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

    @Test
    void givenADraftOrderWithItems_whenTryToRemoveNonexistentItem_shouldThrowException() {
        final Order order = OrderTestDataBuilder.anOrder().build();
        final OrderItemId orderItemId = new OrderItemId();

        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(orderItemId))
                .withMessage("Order %s does not contain order item %s".formatted(order.id(), orderItemId));
    }

    @ParameterizedTest
    @EnumSource(names = { "DRAFT" }, mode = EnumSource.Mode.EXCLUDE)
    void givenANonDraftOrder_whenTryToRemoveItem_shouldThrowException(final OrderStatus nonDraftStatus) {
        final Order order = OrderTestDataBuilder.anOrder().status(nonDraftStatus).build();
        final OrderItemId orderItemId = order.getItems().iterator().next().id();

        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(orderItemId))
                .withMessage("Order %s with status %s cannot be edited".formatted(order.id(), order.getStatus()));
    }

}
