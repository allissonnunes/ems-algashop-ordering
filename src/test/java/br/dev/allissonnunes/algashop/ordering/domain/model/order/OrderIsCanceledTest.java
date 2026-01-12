package br.dev.allissonnunes.algashop.ordering.domain.model.order;

import br.dev.allissonnunes.algashop.ordering.domain.model.customer.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIsCanceledTest {

    @Test
    void givenCanceledOrder_whenIsCanceled_thenReturnTrue() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        assertThat(order.isCanceled()).isTrue();

        final Order draftOrder = Order.draft(new CustomerId());
        assertThat(draftOrder.isCanceled()).isFalse();
        draftOrder.cancel();
        assertThat(draftOrder.isCanceled()).isTrue();
    }

    @Test
    void givenNotCanceledOrder_whenIsCanceled_thenReturnFalse() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build();
        assertThat(order.isCanceled()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(names = { "CANCELED" }, mode = EnumSource.Mode.EXCLUDE)
    void givenAnyOrderStatus_whenIsCanceled_thenReturnFalse(final OrderStatus status) {
        Order order = OrderTestDataBuilder.anOrder().status(status).build();
        assertThat(order.isCanceled()).isFalse();
    }

}
